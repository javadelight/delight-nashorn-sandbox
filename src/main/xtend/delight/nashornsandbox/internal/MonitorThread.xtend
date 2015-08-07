package delight.nashornsandbox.internal

import java.lang.management.ManagementFactory
import java.util.concurrent.atomic.AtomicBoolean

class MonitorThread extends Thread {
	
	val long maxCPUTime
	val AtomicBoolean stop
	val AtomicBoolean operationInterrupted
	val Thread threadToMonitor
	val Runnable onInvalid
	val AtomicBoolean cpuLimitExceeded;
	
	override run() {
	
		while (!stop.get) {
			val bean = ManagementFactory.getThreadMXBean()
			
			val threadCPUTime = bean.getThreadCpuTime(threadToMonitor.id)
			
			if (threadCPUTime > maxCPUTime) {
				cpuLimitExceeded.set(true)
				stop.set(true)
				onInvalid.run
				Thread.sleep(20)
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
	
	def void notifyOperationInterrupted() {
		this.operationInterrupted.set(true)
	}
	
	def void isCPULimitExceeded() {
		this.cpuLimitExceeded.get
	}
	
	new (long maxCPUTimne, Thread threadToMonitor, Runnable onInvalid) {
		this.maxCPUTime = maxCPUTimne
		this.threadToMonitor = threadToMonitor
		this.onInvalid = onInvalid
		this.stop = new AtomicBoolean(false)
		this.operationInterrupted = new AtomicBoolean(false)
		this.cpuLimitExceeded = new AtomicBoolean(false)
	}
}