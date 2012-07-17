package com.zsoleszpapper.mifarestuff;

public interface MifareCreditEvents {
	public void FormatBlock(int block);
	public void ReadValueBlock(int block, int value);
	public void DecrementBlock(int block);
}
