package delight.nashornsandbox.internal

import java.lang.management.ManagementFactory

class SaveThread  {
	
	
	
	
	static class MonitorThread extends Thread {
		
		
		
		
	}
	
	def test() {
		val bean = ManagementFactory.getThreadMXBean()
		bean.setThreadContentionMonitoringEnabled(true)
		bean.setThreadCpuTimeEnabled(true)
		
		val threadInfo = bean.getThreadInfo(Thread.currentThread.id)
		
		threadInfo.
	}

	
}