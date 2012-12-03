package com.hiwayne.commons.serialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.SerializerFactory;

/**
 * 
 * @author wangwei
 * 
 */
public class HessianSerialize<T extends Serializable> implements Serializer<T> {
	private SerializerFactory factory = new SerializerFactory();

	/**
	 * hession序列号对象
	 * 
	 * @param obj
	 * @return
	 * @throws IOException
	 */
	@Override
	public byte[] serialize(T instance) {

		byte[] reByte;
		if (instance == null)
			throw new NullPointerException();
		ByteArrayOutputStream os = null;
		Hessian2Output h2o = null;
		try {
			os = new ByteArrayOutputStream();
			h2o = new Hessian2Output(os);
			h2o.setSerializerFactory(factory);
			h2o.writeObject(instance);
			h2o.flush();
			reByte = os.toByteArray();

			return reByte;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (os != null)
				try {
					os.close();
				} catch (IOException e1) {
				}
			if (h2o != null)
				try {
					h2o.close();
				} catch (IOException e) {
				}
		}

	}

	/**
	 * 反序列号
	 * 
	 * @param by
	 * @return
	 * @throws IOException
	 */
	@Override
	@SuppressWarnings({ "unchecked" })
	public T deserialize(byte[] data) {
		if (data == null)
			throw new NullPointerException();
		ByteArrayInputStream is = new ByteArrayInputStream(data);
		Hessian2Input h2i = new Hessian2Input(is);
		h2i.setSerializerFactory(factory);
		try {
			return (T) h2i.readObject();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (h2i != null)
				try {
					h2i.close();
				} catch (IOException e) {
				}
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
				}
		}
	}

	public static void main(String[] arg) {
		long begin = System.currentTimeMillis();
		HessianSerialize<Date> aHessianSerialize = new HessianSerialize<Date>();
		Date old = null;
		Object newobj = null;
		for (int i = 0; i < 5; i++) {
			begin = System.currentTimeMillis();
			for (int j = 0; j < 10000; j++) {
				old = new Date();
				byte[] abyte;

				try {
					abyte = aHessianSerialize.serialize(old);
					newobj = aHessianSerialize.deserialize(abyte);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			try {
				TimeUnit.MILLISECONDS.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("ok="
					+ (System.currentTimeMillis() - begin - 1000) + ",newobj="
					+ newobj);

		}
	}
}
