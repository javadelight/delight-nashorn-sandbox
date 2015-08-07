package delight.nashornsandbox.internal

class InterruptTest {
	
	def static isInterrupted() {
		println('test '+Thread.currentThread.interrupted)
		Thread.currentThread.interrupted
	}
	
}