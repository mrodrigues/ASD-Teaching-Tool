package com.example.asdteachingtool.models;


public interface Sortable  {
	public abstract void beforeSave();
	public abstract void secureSave();
	public abstract Integer getPosition();
	public abstract Long getId();
	public abstract String getName();
	public abstract void setPosition(Integer position);
}
