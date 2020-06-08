package Lab6_HW;
import simView.*;
import genDevs.modeling.*;
import GenCol.*;

public class transd extends  ViewableAtomic {
	
	protected Function arrived, solved;						// transducer에 도착한 request를 arrived에, 처리된 request를 solved에 저장
	protected double clock, total_ta, observation_time;		// 누적 실행 시간, total turnaround time, observation_time 저장
	protected int arrived_cnt, solved_cnt;					// 도착한 request, 처리된 request의 번호 저장

	// constructor
	public transd(String name, double Observation_time) {
		super(name);
		
		// out, in port 설정
		addOutport("out");
		addInport("ariv");
		addInport("solved");
    
		// arrived, solved 객체 생성
		arrived = new Function();
		solved = new Function();
		// arrived_cnt(도착한 request 개수), solved_cnt(처리된 request 개수) 초기화
		arrived_cnt = 0;
		solved_cnt = 0;
    
		observation_time = Observation_time;
	}
	// constructor
	public transd()	{
		this("transd", 200);
	}
	
	// 변수, 객체 초기화
	public void initialize() {	
		clock = 0;
		total_ta = 0;
 
		arrived = new Function();
		solved = new Function();
		arrived_cnt = 0;
		solved_cnt = 0;
		holdIn("on", observation_time);
	}
	
	// client, server, server2에서 도착한 request의 turnaround time, throughput 계산
	public void deltext(double e, message x) {
		// clock에 event가 발생한 시간 더함
		clock = clock + e;	
		Continue(e);
		entity val;
 
		if(phaseIs("on")) {
			for (int i = 0; i < x.size(); i++)	{
				// client의 request가 transducer의 "ariv"에 도착
				if (messageOnPort(x, "ariv", i)) {
					val = x.getValOnPort("ariv", i);
					// name을 "job + (도착한 순서(arrived_cnt))" 형태로 저장
					String name = "job " + Integer.toString(arrived_cnt++);
					// name을 arrived에 삽입
					arrived.put(name, new doubleEnt(clock));
				}
				// server, server2의 result가 transducer의 "solved"에 도착
				if (messageOnPort(x, "solved", i))	{
					val = x.getValOnPort("solved", i);
					// name을 "job + (처리한 순서(solved_cnt))" 형태로 저장
					String name = "job " + Integer.toString(solved_cnt++);
					// arrived에 job + (처리한 순서(solved_cnt))가 존재하면 turnaround time 계산
					if (arrived.containsKey(name))	{
						entity ent = (entity) arrived.assoc(name);
						doubleEnt num = (doubleEnt) ent;
						double arrival_time = num.getv();
						double turn_around_time = clock - arrival_time;
						total_ta = total_ta + turn_around_time;
						// solved에 삽입
						solved.put(val, new doubleEnt(clock));
					}
				}
			}
			// 처리 결과 표시
			System.out.println("--------------------------------------------------------");
	   		show_state();
	   		System.out.println("--------------------------------------------------------");
		}
	}
	// 처리 결과 표시
	public void deltint() {
		if (phaseIs("on")) {
			clock = clock + sigma;
			// 처리 결과 표시
			System.out.println("--------------------------------------------------------");
	   		show_state();
	   		System.out.println("--------------------------------------------------------");
	   		
	   		holdIn("off", 0);
		}
	}
	// 처리 결과를 client에 전송
	public message out() {
		message m = new message();
		
		if (phaseIs("on"))
			m.add(makeContent("out", new entity("TA: " + compute_TA())));
		return m;
	}

	// turnaround time 계산
	public double compute_TA() {
		double avg_ta_time = 0;
		if (!solved.isEmpty())
			avg_ta_time = ( (double) total_ta) / solved.size();
		
		return avg_ta_time;
	}

	// throughput 계산
	public String compute_Thru() {
		String thruput = "";
		if (clock > 0)
			thruput = solved.size() + " / " + clock;
		
		return thruput;
	}

	// 처리 결과 표시
	public void show_state() {
		System.out.println("■ state of  " + name + ": ");
		System.out.println("- phase, sigma : " + phase + " " + sigma + " ");
		
		// 도착한 request의 수, 처리된 request의 수, total turnaround time, average turnaround time, throughput 출력
		if (arrived != null && solved != null)	{
			System.out.println("- Total jobs arrived : "+ arrived.size());
			System.out.println("- Total jobs solved : " + solved.size());
			System.out.println("  ● Total TA = " + total_ta);
			System.out.println("  ● AVG TA = " + compute_TA());
			System.out.println("  ● THRUPUT = " + compute_Thru());
		}
	}	
  
	public String getTooltipText() {
		String s = "";
		if (arrived != null && solved != null)	{
			s = "\n" + "jobs arrived :" + arrived.size()
			+ "\n" + "jobs solved :" + solved.size()
			+ "\n" + "AVG TA = " + compute_TA()
			+ "\n" + "THRUPUT = " + compute_Thru();
		}
		return super.getTooltipText() + s;
	}

}
