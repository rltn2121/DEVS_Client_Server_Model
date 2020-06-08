package Lab6_HW;
import GenCol.*;

public class result extends entity{
	// request 처리 결과를 저장하는 변수
	int result;
	// constructor
	public result(String name, int _result)	{  
		// request의 처리 결과인 result를 받음
		super(name);  
		result = _result;
	}
}
