package beans;

import java.util.Date;

public class TSLimits {

	private long mTSStart, mTSEnd;

	public TSLimits(long pTSStart, long pTSEnd) {
		super();
		this.mTSStart = pTSStart;
		this.mTSEnd = pTSEnd;
	}

	public long getTSStart() {
		return mTSStart;
	}

	public void setTSStart(long pTSStart) {
		this.mTSStart = pTSStart;
	}

	public long getTSEnd() {
		return mTSEnd;
	}

	public void setTSEnd(long pTSEnd) {
		this.mTSEnd = pTSEnd;
	}

	public TSLimits() {
	}

	public boolean contains(Date pDate) {
		return new Date(this.mTSStart).before(pDate) && new Date(this.mTSEnd).after(pDate);
	}

	@Override
	public String toString() {
		return "TSLimits [mTSStart=" + mTSStart + ", mTSEnd=" + mTSEnd + "]";
	}
}
