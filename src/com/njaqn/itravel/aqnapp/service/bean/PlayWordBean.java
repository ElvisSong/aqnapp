package com.njaqn.itravel.aqnapp.service.bean;

public class PlayWordBean {

	private String word;       //���Ŵ�
	private int playCount;  //���Ŵ���
	private int wordType;      //����
	private String key;     //Ψһ��ʾ����
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public int getWordType() {
		return wordType;
	}
	public void setWordType(int wordType) {
		this.wordType = wordType;
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public int getPlayCount() {
		return playCount;
	}
	public void setPlayCount(int playCount) {
		this.playCount = playCount;
	}
}
