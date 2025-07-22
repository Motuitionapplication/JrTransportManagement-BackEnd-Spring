package com.playschool.management;

public class AdditionLogic { //class object

	int  addMyParam(int a, int b)	
	{
		int c = a+b;
		
		return c;
	}
	
	
	
	public static void main(String  args[])
	{
		
		AdditionLogic  objectname = new AdditionLogic();
		
		int result = objectname.addMyParam(2,3);
		
		System.out.println("This is my out put "+result);
	}
}
