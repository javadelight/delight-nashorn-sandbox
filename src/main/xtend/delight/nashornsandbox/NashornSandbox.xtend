package delight.nashornsandbox

interface NashornSandbox {
	def void allow(Class<?> clazz) 
	def Object eval(String js)
}