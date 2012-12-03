package com.hiwayne.commons.serialize;

public interface Serializer<T> {

	public byte[] serialize(T instance);

	public T deserialize(byte[] data);
}