package delight.nashornsandbox.internal

import java.lang.management.ManagementFactory
import java.util.concurrent.atomic.AtomicBoolean

class MonitorThread extends Thread {
	
	
	val long maxCPUTime
	val AtomicBoolean stop
	val AtomicBoolean operationInterrupted
	var Thread threadToMonitor
	var Runnable onInvalid
	val AtomicBoolean cpuLimitExceeded;
	
	override run() {
		val bean = ManagementFactory.getThreadMXBean()
		val startCPUTime = bean.getThreadCpuTime(threadToMonitor.id)
		while (!stop.get) {
			
			
			val threadCPUTime = bean.getThreadCpuTime(threadToMonitor.id)
			
			val runtime = threadCPUTime -startCPUTime
			
			if ((runtime) > maxCPUTime) {
				cpuLimitExceeded.set(true)
				stop.set(true)
				onInvalid.run
				Thread.sleep(50)
				if (this.operationInterrupted.get() == false) {
					println(MonitorThread.this+': Thread hard shutdown!')
					threadToMonitor.stop
				}
				return
			}
			
			Thread.sleep(5)
		}
	
	}
	
	def void stopMonitor() {
		stop.set(true)
	}
	
	def void setThreadToMonitor(Thread t) {
		this.threadToMonitor = t
	}
	
	def void setOnInvalidHandler(Runnable r) {
		this.onInvalid = r
	}
	
	def void notifyOperationInterrupted() {
		this.operationInterrupted.set(true)
	}
	
	def isCPULimitExceeded() {
		this.cpuLimitExceeded.get
	}
	
	def gracefullyInterrputed() {
		this.operationInterrupted.get()
	}
	
	new (long maxCPUTimne) {
		this.maxCPUTime = maxCPUTimne
		
		
		this.stop = new AtomicBoolean(false)
		this.operationInterrupted = new AtomicBoolean(false)
		this.cpuLimitExceeded = new AtomicBoolean(false)
	}
}