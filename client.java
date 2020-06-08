package Lab6_HW;
import simView.*;
import genDevs.modeling.*;
import GenCol.*;

public class client extends ViewableAtomic {
	protected double int_arr_time;  // request를 전송하는 간격
	protected request req_msg;      // request를 전송할 message
	protected result res_msg;       // result를 전송할 message
	protected int num1, num2;
	protected char op;
  
	// constructor
	public client() {
		this("client", 30);
	}
  
	// constructor
	public client(String name, double Int_arr_time)	{
		super(name);
		// out port 설정
		addOutport("out_server");
		addOutport("out_server2");
		// in port 설정 
		addInport("in");
		int_arr_time = Int_arr_time;
	}
	// 변수, 객체 초기화
	public void initialize() {
		// req_msg, res_msg 초기화
		req_msg = new request("none",0,0, ' ');
		res_msg = new result("none",0);
		
		// num1, num2, op를 무작위 값으로 초기화
		num1 = (int)(Math.random()*100)+1;
		num2 = (int)(Math.random()*100)+1;
		int op_num = (int)(Math.random()*4);
		switch(op_num){
		case 0:
			op = '+';
			break;
		case 1:
			op = '-';
			break;
		case 2:
			op = '*';
			break;
		case 3:
			op = '%';
			break;
		}
		holdIn("active", int_arr_time);
	}
	// server, server2로부터 받은 result를 처리
	public void deltext(double e, message x) {
		Continue(e);
		if (phaseIs("active"))	{
			for (int i = 0; i < x.getLength(); i++)	{
				if (messageOnPort(x, "in", i))	
					// server, server2로 부터 받은 result를 콘솔창에 출력
					System.out.println("  ● result of request = " + x.getValOnPort("in", i));
			}
		}
	}
	
	// num1, num2, op 값 설정
	public void deltint() {
		if (phaseIs("active")) {
			// num1, num2, op를 무작위로 설정
			num1 = (int)(Math.random()*100)+1;
			num2 = (int)(Math.random()*100)+1;
			int op_num = (int)(Math.random()*4);
			switch(op_num){
			case 0:
				op = '+';
				break;
			case 1:
				op = '-';
				break;
			case 2:
				op = '*';
				break;
			case 3:
				op = '%';
				break;
			}
			holdIn("active", int_arr_time);
		}
	}
	
	// deltint()에서 생성된 request를 transducer와 server(server2)로 보냄
	public message out() {
		message m = new message();
		String name = Integer.toString(num1) + op + Integer.toString(num2);
		req_msg = new request(name, num1, num2, op); 
		// op가 * or % 이면 server로 보냄
		if(op=='*' || op=='%')
			m.add(makeContent("out_server", req_msg));
		// op가 + or - 이면 server2로 보냄
		else
			m.add(makeContent("out_server2", req_msg));
		return m;
	}
  
	public String getTooltipText() {
		return super.getTooltipText();
	}
}
