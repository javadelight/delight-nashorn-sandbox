package delight.nashornsandbox.internal;

import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;

public class SecureInterruptibleCharSequence  implements CharSequence {
   final CharSequence inner;
    final ThreadLocal<Integer> matchCount;

		public SecureInterruptibleCharSequence(CharSequence inner, ThreadLocal<Integer> matchCount) {
			super();
			this.inner = inner;
      this.matchCount = matchCount;
		}

		@Override
		public char charAt(int index) {
			matchCount.set(matchCount.get()+1);
			if (matchCount.get() > 5000000){
				Thread thread = Thread.currentThread();
				thread.interrupt();
			}
			if (Thread.currentThread().isInterrupted()) {
				throw new ScriptCPUAbuseException("Regular expression running for too many iterations.", true, null);
			}
			return inner.charAt(index);
		}

		@Override
		public int length() {
			return inner.length();
		}

		@Override
		public CharSequence subSequence(int start, int end) {
			return new SecureInterruptibleCharSequence(inner.subSequence(start, end), matchCount);
		}

		@Override
		public String toString() {
			return inner.toString();
		}
}
