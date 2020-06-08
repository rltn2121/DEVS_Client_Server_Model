package Lab6_HW;
import genDevs.modeling.*;
import GenCol.*;
import Lab6.request_msg;
import Lab6.result_msg;
import simView.*;

public class server2 extends ViewableAtomic{
	protected Queue q;						// request를 저장할 queue
	protected request req_msg;				// request를 전송할 message
	protected result res_msg;				// result를 전송할 message
	protected double processing_time;		// request를 처리하는데 걸리는 시간
	protected int result;
	
	public server2()	{
		this("server2", 20);
	}

	public server2(String name, double Processing_time)	{
		super(name);
		// in port, out port 설정
		addInport("in");
		addOutport("out");
		processing_time = Processing_time;
	}
	
	// 변수, 객체 초기화
	public void initialize()	{
		result = 0;
		processing_time = 0;
		q = new Queue();
		req_msg = new request("none",0,0, ' ');
		res_msg = new result("none",0);
		holdIn("passive", INFINITY);
	}

	// client에서 전송한 request 처리
	public void deltext(double e, message x) {
		Continue(e);
		// 처리 중인 request가 없을 경우
		if (phaseIs("passive"))		{
			for (int i = 0; i < x.size(); i++) {
				if (messageOnPort(x, "in", i)) {
					// in port로 들어온 request를 받아서 처리 
					req_msg = (request)x.getValOnPort("in", i);
					// op가 + 이면 processing_time = 20, -이면 processing_time = 30으로 설정
				switch(req_msg.op){
				case '+':
					result = req_msg.num1 + req_msg.num2;
					holdIn("busy", 20);
					break;
				case '-':
					result = req_msg.num1 - req_msg.num2;
					holdIn("busy", 30);
					break;
				
				}
			}
		}
	}
		// request를 처리 중인 경우
		else if (phaseIs("busy")) {
			for (int i = 0; i < x.size(); i++) {
				if (messageOnPort(x, "in", i)) {
					// in port로 들어온 request를 받아서 queue에 삽입
					request temp = (request)x.getValOnPort("in", i);
					q.add(temp);
				}
			}
		}
	}
	
	public void deltint() {
		if (phaseIs("busy")) {
			// queue에 처리하지 않은 request가 남은 경우
			if (!q.isEmpty()) {
				// queue에서 request를 꺼내서 처리
				req_msg = (request)q.removeFirst();
				// op가 + 이면 processing_time = 20, -이면 processing_time = 30으로 설정
				switch(req_msg.op){
				case '+':
					result = req_msg.num1 + req_msg.num2;
					holdIn("busy", 10);
					break;
				case '-':
					result = req_msg.num1 - req_msg.num2;
					holdIn("busy", 20);
					break;
				}

			}
			// queue가 비었을 경우 초기화
			else{
				result = 0;
				req_msg = new request("none",0,0, ' ');
				res_msg = new result("none",0);
				processing_time = 0;
				q.clear();
				holdIn("passive", INFINITY);
			}
		}
	}
	// request의 처리 결과를 client로 보냄
	public message out() {
		message m = new message();
		if (phaseIs("busy")) {
			res_msg = new result(Integer.toString(result), result);
			m.add(makeContent("out", res_msg));
		}
		return m;
	}	
	
	public String getTooltipText() {
		return
        super.getTooltipText()
        + "\n" + "queue length: " + q.size()
        + "\n" + "queue itself: " + q.toString();
	}

}



