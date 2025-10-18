#!/usr/bin/env node
/**
 * repo-audit.js (improved)
 *
 * Improvements made:
 * - Skip heavy folders by default: node_modules, .git, dist, build, target
 * - Accept --exclude=folder1,folder2 to add custom ignores
 * - Iterative directory walk (stack) to avoid deep recursion and improve speed
 * - Progress logging every N files and elapsed time summary
 * - More robust API extraction (fetch, axios, HttpClient, template strings)
 * - Handles being run from anywhere and accepts absolute or relative paths
 * - Writes tools/audit-report.md and tools/audit-report.json (same as before)
 * - Exits 0 on success and non-zero for serious IO errors
 *
 * Usage:
 *   node tools/repo-audit.js --frontend=../frontend --backend=../backend [--exclude=node_modules,.git]
 *
 */

const fs = require("fs");
const path = require("path");

// ---------- CLI ARGS ----------
const argv = process.argv.slice(2);
const defaults = { frontend: "frontend", backend: "backend", exclude: "" };
const options = { ...defaults };
for (const a of argv) {
  const m = a.match(/^--([^=]+)=(.+)$/);
  if (m) options[m[1]] = m[2];
}

const cwd = process.cwd();
const FRONTEND_DIR = path.resolve(cwd, options.frontend);
const BACKEND_DIR = path.resolve(cwd, options.backend);
const TOOLS_DIR = path.resolve(cwd, "tools");
const REPORT_MD = path.join(TOOLS_DIR, "audit-report.md");
const REPORT_JSON = path.join(TOOLS_DIR, "audit-report.json");

// default ignore list + allow user-specified excludes
const DEFAULT_IGNORES = ["node_modules", ".git", "dist", "build", "target"];
const userIgnores = options.exclude
  ? options.exclude
      .split(",")
      .map((s) => s.trim())
      .filter(Boolean)
  : [];
const IGNORES = Array.from(new Set(DEFAULT_IGNORES.concat(userIgnores))).map(
  (s) => s.toLowerCase()
);

// Ensure tools directory exists
try {
  if (!fs.existsSync(TOOLS_DIR)) fs.mkdirSync(TOOLS_DIR, { recursive: true });
} catch (e) {
  console.error("Error ensuring tools directory exists:", e.message);
  process.exit(2);
}

// ---------- HELPERS ----------
function safeRead(filePath) {
  try {
    return fs.readFileSync(filePath, "utf8");
  } catch {
    return "";
  }
}

function shouldIgnore(name) {
  if (!name) return false;
  const n = name.toLowerCase();
  // ignore if name equals or contains ignore token
  return IGNORES.some((ignore) => n === ignore || n.includes(ignore));
}

// iterative directory walk with ignore list and progress
function walkDirIterative(rootDir, onProgress) {
  const results = [];
  if (!fs.existsSync(rootDir)) return results;
  const stack = [rootDir];
  let scanned = 0;
  const start = Date.now();

  while (stack.length) {
    const dir = stack.pop();
    let entries;
    try {
      entries = fs.readdirSync(dir, { withFileTypes: true });
    } catch (e) {
      continue;
    }

    for (const entry of entries) {
      if (shouldIgnore(entry.name)) continue;
      const full = path.join(dir, entry.name);
      try {
        if (entry.isDirectory()) stack.push(full);
        else if (entry.isFile()) {
          results.push(full);
          scanned++;
          if (onProgress && scanned % 1000 === 0)
            onProgress(scanned, Date.now() - start);
        }
      } catch {}
    }
  }
  if (onProgress) onProgress(scanned, Date.now() - start, true);
  return results;
}

function countMatchesInFiles(files, regex) {
  let count = 0;
  for (const f of files) if (regex.test(safeRead(f))) count++;
  return count;
}

function findFilesMatching(files, regex) {
  const matched = [];
  for (const f of files) if (regex.test(safeRead(f))) matched.push(f);
  return matched;
}

function findFirstFilesMatching(files, regex, limit = 10) {
  const matched = [];
  for (const f of files) {
    if (regex.test(safeRead(f))) {
      matched.push(f);
      if (matched.length >= limit) break;
    }
  }
  return matched;
}

function searchNamesPresence(files, names) {
  const found = [];
  const missing = [];
  for (const name of names) {
    // word boundary match
    const rx = new RegExp("\\b" + name + "\\b");
    const isFound = files.some((f) => rx.test(safeRead(f)));
    (isFound ? found : missing).push(name);
  }
  return { found, missing };
}

function extractFrontendApiStrings(files) {
  // capture /api/... inside '', "", or template strings and common patterns
  const samples = new Set();
  const patterns = [
    /['"`]\s*(\/api\/[\w\-\/.\?=&%:;@+\[\]~]*)['"`]/g,
    /fetch\(\s*['"`]\s*(\/api\/[\w\-\/.\?=&%:;@+\[\]~]*)['"`]/g,
    /axios\.(?:get|post|put|delete)\([^'"`]*['"`]\s*(\/api\/[\w\-\/.\?=&%:;@+\[\]~]*)['"`]/gi,
    /HttpClient\.(?:get|post|put|delete)\([^'"`]*['"`]\s*(\/api\/[\w\-\/.\?=&%:;@+\[\]~]*)['"`]/gi,
  ];
  for (const f of files) {
    const content = safeRead(f);
    for (const p of patterns) {
      let m;
      while ((m = p.exec(content)) !== null) {
        samples.add(m[1]);
        if (samples.size >= 50) break;
      }
      if (samples.size >= 50) break;
    }
    if (samples.size >= 50) break;
  }
  return Array.from(samples);
}

function checkBackendHasPath(files, apiPath) {
  // look for the path literal or path fragments (naive but useful)
  try {
    const rx = new RegExp(apiPath.replace(/[.*+?^${}()|[\]\\]/g, "\\$&"));
    return files.some((f) => rx.test(safeRead(f)));
  } catch {
    return false;
  }
}

// ---------- MAIN ----------
(function main() {
  const runInfo = { runAt: new Date().toISOString(), cwd, options };

  const result = {
    runInfo,
    frontend: {
      dir: FRONTEND_DIR,
      exists: fs.existsSync(FRONTEND_DIR),
      totalFiles: 0,
      componentCount: 0,
      injectableCount: 0,
      expectedNames: {
        toCheck: [
          "DriverLoginComponent",
          "DriverDashboardComponent",
          "DriverProfileComponent",
          "VehicleComponent",
          "ConsignmentComponent",
          "HistoryComponent",
          "DocumentUploadComponent",
          "ReviewComponent",
        ],
        found: [],
        missing: [],
      },
      jwtPatterns: {},
      mockOrHardcoded: {},
      sampleComponentFiles: [],
      apiSamples: [],
    },
    backend: {
      dir: BACKEND_DIR,
      exists: fs.existsSync(BACKEND_DIR),
      totalFiles: 0,
      controllerFiles: [],
      mappingAnnotations: { get: 0, post: 0, put: 0, del: 0, request: 0 },
      expectedNames: {
        toCheck: [
          "DriverAuthController",
          "DriverController",
          "DriverAuthService",
          "DriverService",
          "JWTFilter",
          "SecurityConfig",
        ],
        found: [],
        missing: [],
      },
      configs: {
        hasApplicationProperties: false,
        hasApplicationYml: false,
        flags: {
          jwtSecret: false,
          datasourceUser: false,
          datasourcePass: false,
          postgresJdbc: false,
        },
      },
      realtimeOrMigrations: {
        websocket: false,
        stomp: false,
        fcm: false,
        flyway: false,
        liquibase: false,
      },
      sampleControllerFiles: [],
    },
    crossCheck: { frontendApiSamples: [], missingInBackend: [] },
  };

  // FRONTEND
  try {
    const feFiles = result.frontend.exists
      ? walkDirIterative(FRONTEND_DIR, (count, ms, done) => {
          if (!done && count % 1000 === 0)
            console.log(
              `Scanning frontend files: ${count} (elapsed ${Math.round(
                ms / 1000
              )}s)`
            );
          if (done)
            console.log(
              `Finished frontend scan: ${count} files in ${Math.round(
                ms / 1000
              )}s`
            );
        })
      : [];
    result.frontend.totalFiles = feFiles.length;
    result.frontend.componentCount = countMatchesInFiles(
      feFiles,
      /@Component\s*\(/
    );
    result.frontend.injectableCount = countMatchesInFiles(
      feFiles,
      /@Injectable\s*\(/
    );
    const presence = searchNamesPresence(
      feFiles,
      result.frontend.expectedNames.toCheck
    );
    result.frontend.expectedNames.found = presence.found;
    result.frontend.expectedNames.missing = presence.missing;
    const feAllContent = feFiles.map(safeRead).join("\n");
    result.frontend.jwtPatterns.localStorageSetToken =
      /localStorage\.setItem\(\s*['"]token['"]/i.test(feAllContent);
    result.frontend.jwtPatterns.sessionStorageSetToken =
      /sessionStorage\.setItem\(\s*['"]token['"]/i.test(feAllContent);
    result.frontend.jwtPatterns.authorizationHeader =
      /Authorization\s*[:=]/i.test(feAllContent);
    result.frontend.mockOrHardcoded.hasMockKeyword = /\bmock\b/i.test(
      feAllContent
    );
    result.frontend.mockOrHardcoded.hasConstArray =
      /const\s+\w+\s*=\s*\[[^\]]*\]/i.test(feAllContent);
    result.frontend.sampleComponentFiles = findFirstFilesMatching(
      feFiles,
      /@Component\s*\(/,
      10
    ).map((f) => path.relative(cwd, f));
    result.frontend.apiSamples = extractFrontendApiStrings(feFiles);
  } catch (e) {
    console.warn("Frontend scan issue:", e.message);
  }

  // BACKEND
  try {
    const beFiles = result.backend.exists
      ? walkDirIterative(BACKEND_DIR, (count, ms, done) => {
          if (!done && count % 1000 === 0)
            console.log(
              `Scanning backend files: ${count} (elapsed ${Math.round(
                ms / 1000
              )}s)`
            );
          if (done)
            console.log(
              `Finished backend scan: ${count} files in ${Math.round(
                ms / 1000
              )}s`
            );
        })
      : [];
    result.backend.totalFiles = beFiles.length;
    result.backend.controllerFiles = findFilesMatching(
      beFiles,
      /@(RestController|Controller)\b/
    ).map((f) => path.relative(cwd, f));
    result.backend.mappingAnnotations.get = countMatchesInFiles(
      beFiles,
      /@GetMapping\b/
    );
    result.backend.mappingAnnotations.post = countMatchesInFiles(
      beFiles,
      /@PostMapping\b/
    );
    result.backend.mappingAnnotations.put = countMatchesInFiles(
      beFiles,
      /@PutMapping\b/
    );
    result.backend.mappingAnnotations.del = countMatchesInFiles(
      beFiles,
      /@DeleteMapping\b/
    );
    result.backend.mappingAnnotations.request = countMatchesInFiles(
      beFiles,
      /@RequestMapping\b/
    );
    const presenceB = searchNamesPresence(
      beFiles,
      result.backend.expectedNames.toCheck
    );
    result.backend.expectedNames.found = presenceB.found;
    result.backend.expectedNames.missing = presenceB.missing;
    const propertiesFiles = beFiles.filter((f) =>
      /application\.properties$/i.test(f)
    );
    const ymlFiles = beFiles.filter((f) => /application\.(yml|yaml)$/i.test(f));
    result.backend.configs.hasApplicationProperties =
      propertiesFiles.length > 0;
    result.backend.configs.hasApplicationYml = ymlFiles.length > 0;
    const configContent = propertiesFiles
      .concat(ymlFiles)
      .map(safeRead)
      .join("\n");
    result.backend.configs.flags.jwtSecret = /jwt\.secret\s*[=:]/i.test(
      configContent
    );
    result.backend.configs.flags.datasourceUser =
      /spring\.datasource\.username\s*[=:]/i.test(configContent);
    result.backend.configs.flags.datasourcePass =
      /spring\.datasource\.password\s*[=:]/i.test(configContent);
    result.backend.configs.flags.postgresJdbc = /jdbc:postgresql/i.test(
      configContent
    );
    const beAllContent = beFiles.map(safeRead).join("\n");
    result.backend.realtimeOrMigrations.websocket =
      /@EnableWebSocket|WebSocket/i.test(beAllContent);
    result.backend.realtimeOrMigrations.stomp =
      /STOMP|SimpMessagingTemplate|@EnableWebSocketMessageBroker/i.test(
        beAllContent
      );
    result.backend.realtimeOrMigrations.fcm = /Firebase|FCM/i.test(
      beAllContent
    );
    result.backend.realtimeOrMigrations.flyway = /Flyway/i.test(beAllContent);
    result.backend.realtimeOrMigrations.liquibase = /Liquibase/i.test(
      beAllContent
    );
    result.backend.sampleControllerFiles = result.backend.controllerFiles.slice(
      0,
      10
    );

    // cross-check
    result.crossCheck.frontendApiSamples = result.frontend.apiSamples.slice(
      0,
      50
    );
    const missing = [];
    for (const apiPath of result.crossCheck.frontendApiSamples) {
      const has = checkBackendHasPath(beFiles, apiPath);
      if (!has) missing.push(apiPath);
    }
    result.crossCheck.missingInBackend = missing;
  } catch (e) {
    console.warn("Backend scan issue:", e.message);
  }

  // WRITE REPORTS
  try {
    const md = [];
    md.push("# Repo Audit Report");
    md.push("");
    md.push(`- Generated: ${result.runInfo.runAt}`);
    md.push(`- CWD: ${result.runInfo.cwd}`);
    md.push(
      `- Options: frontend=${options.frontend}, backend=${options.backend}, exclude=${options.exclude}`
    );
    md.push("");

    md.push("## Frontend");
    md.push(`- Directory: ${result.frontend.dir}`);
    md.push(`- Exists: ${result.frontend.exists}`);
    md.push(`- Total files: ${result.frontend.totalFiles}`);
    md.push(`- @Component files: ${result.frontend.componentCount}`);
    md.push(`- @Injectable files: ${result.frontend.injectableCount}`);
    md.push("");
    md.push("- Expected components/services presence:");
    md.push(
      `  - Found: ${result.frontend.expectedNames.found.join(", ") || "None"}`
    );
    md.push(
      `  - Missing: ${
        result.frontend.expectedNames.missing.join(", ") || "None"
      }`
    );
    md.push("");
    md.push("- JWT patterns:");
    md.push(
      `  - localStorage.setItem('token'): ${result.frontend.jwtPatterns.localStorageSetToken}`
    );
    md.push(
      `  - sessionStorage.setItem('token'): ${result.frontend.jwtPatterns.sessionStorageSetToken}`
    );
    md.push(
      `  - Authorization header present: ${result.frontend.jwtPatterns.authorizationHeader}`
    );
    md.push("");
    md.push("- Mock/hard-coded indicators:");
    md.push(
      `  - Contains word "mock": ${result.frontend.mockOrHardcoded.hasMockKeyword}`
    );
    md.push(
      `  - Contains const array literal: ${result.frontend.mockOrHardcoded.hasConstArray}`
    );
    md.push("");
    md.push("- Sample component files:");
    if (result.frontend.sampleComponentFiles.length)
      result.frontend.sampleComponentFiles.forEach((f) => md.push(`  - ${f}`));
    else md.push("  - None");
    md.push("");

    md.push("## Backend");
    md.push(`- Directory: ${result.backend.dir}`);
    md.push(`- Exists: ${result.backend.exists}`);
    md.push(`- Total files: ${result.backend.totalFiles}`);
    md.push(
      `- Controller files detected: ${result.backend.controllerFiles.length}`
    );
    md.push(
      `- Mapping annotations counts: GET=${result.backend.mappingAnnotations.get}, POST=${result.backend.mappingAnnotations.post}, PUT=${result.backend.mappingAnnotations.put}, DELETE=${result.backend.mappingAnnotations.del}, REQUEST=${result.backend.mappingAnnotations.request}`
    );
    md.push("");
    md.push("- Expected classes presence:");
    md.push(
      `  - Found: ${result.backend.expectedNames.found.join(", ") || "None"}`
    );
    md.push(
      `  - Missing: ${
        result.backend.expectedNames.missing.join(", ") || "None"
      }`
    );
    md.push("");
    md.push("- Config files:");
    md.push(
      `  - application.properties present: ${result.backend.configs.hasApplicationProperties}`
    );
    md.push(
      `  - application.yml present: ${result.backend.configs.hasApplicationYml}`
    );
    md.push("  - Flags (values redacted):");
    md.push(`    - jwt.secret: ${result.backend.configs.flags.jwtSecret}`);
    md.push(
      `    - spring.datasource.username: ${result.backend.configs.flags.datasourceUser}`
    );
    md.push(
      `    - spring.datasource.password: ${result.backend.configs.flags.datasourcePass}`
    );
    md.push(
      `    - jdbc:postgresql: ${result.backend.configs.flags.postgresJdbc}`
    );
    md.push("");
    md.push("- Real-time / DB migrations markers:");
    md.push(`  - WebSocket: ${result.backend.realtimeOrMigrations.websocket}`);
    md.push(`  - STOMP: ${result.backend.realtimeOrMigrations.stomp}`);
    md.push(`  - FCM: ${result.backend.realtimeOrMigrations.fcm}`);
    md.push(`  - Flyway: ${result.backend.realtimeOrMigrations.flyway}`);
    md.push(`  - Liquibase: ${result.backend.realtimeOrMigrations.liquibase}`);
    md.push("");
    md.push("- Sample controller files:");
    if (result.backend.sampleControllerFiles.length)
      result.backend.sampleControllerFiles.forEach((f) => md.push(`  - ${f}`));
    else md.push("  - None");
    md.push("");

    md.push("## Cross-check: Frontend API paths vs Backend");
    if (result.crossCheck.frontendApiSamples.length) {
      md.push("- Frontend API samples:");
      result.crossCheck.frontendApiSamples
        .slice(0, 20)
        .forEach((s) => md.push(`  - ${s}`));
    } else md.push("- No API strings detected in frontend.");
    md.push("");
    if (result.crossCheck.missingInBackend.length) {
      md.push("- Missing in backend (string not found in backend sources):");
      result.crossCheck.missingInBackend
        .slice(0, 50)
        .forEach((s) => md.push(`  - ${s}`));
    } else md.push("- No missing API paths detected (string-level match).");
    md.push("");

    md.push("## Recommendations / Next Steps");
    md.push("- Review missing expected components/services and classes.");
    md.push(
      "- Verify JWT handling patterns are consistent with your auth strategy."
    );
    md.push('- Investigate any "mock" or hard-coded arrays left in code.');
    md.push(
      "- For missing API paths, confirm backend mappings or adjust frontend endpoints."
    );
    md.push("- Consider adding tests or CI checks to validate key artifacts.");

    fs.writeFileSync(REPORT_MD, md.join("\n"), "utf8");
    fs.writeFileSync(REPORT_JSON, JSON.stringify(result, null, 2), "utf8");
  } catch (e) {
    console.error("Failed to write reports:", e.message);
    process.exit(3);
  }

  if (!fs.existsSync(FRONTEND_DIR))
    console.warn(`Warning: Frontend directory not found: ${FRONTEND_DIR}`);
  if (!fs.existsSync(BACKEND_DIR))
    console.warn(`Warning: Backend directory not found: ${BACKEND_DIR}`);
  console.log(
    `Audit complete. Reports written to:\n- ${REPORT_MD}\n- ${REPORT_JSON}`
  );
  process.exit(0);
})();
