package com.hiwayne.commons.serialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class JavaSerializer<T extends Serializable> implements Serializer<T> {

	@Override
	@SuppressWarnings({ "unchecked" })
	public T deserialize(byte[] data) {
		ObjectInputStream inputStream = null;
		try {
			inputStream = new ObjectInputStream(new ByteArrayInputStream(data));
			return (T) inputStream.readObject();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}
		}
	}

	@Override
	public byte[] serialize(T instance) {
		ByteArrayOutputStream byteArrayStream = null;
		ObjectOutputStream arrayOutput = null;
		try {
			byteArrayStream = new ByteArrayOutputStream();
			arrayOutput = new ObjectOutputStream(byteArrayStream);
			arrayOutput.writeObject(instance);
			arrayOutput.flush();

			byte[] bytes = byteArrayStream.toByteArray();
			arrayOutput.close();
			return bytes;

		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (byteArrayStream != null)
				try {
					byteArrayStream.close();
				} catch (IOException e) {
				}
			if (arrayOutput != null)
				try {
					arrayOutput.close();
				} catch (IOException e) {
				}
		}
	}
}