package delight.nashornsandbox.internal

class InterruptTest {
	
	def static isInterrupted() {
		
		Thread.currentThread.interrupted
	}
	
}