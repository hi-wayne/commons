package com.viei.commons.serialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
public class HessianSerialize {
	private SerializerFactory factory = new SerializerFactory();

	/**
	 * hession序列号对象
	 * 
	 * @param obj
	 * @return
	 * @throws IOException
	 */
	public byte[] serialize(Object obj) throws IOException {

		byte[] reByte;
		if (obj == null)
			throw new NullPointerException();

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Hessian2Output h2o = new Hessian2Output(os);
		h2o.setSerializerFactory(factory);

		h2o.writeObject(obj);
		h2o.flush();
		h2o.close();

		reByte = os.toByteArray();
		os.close();

		return reByte;
	}

	/**
	 * 反序列号
	 * 
	 * @param by
	 * @return
	 * @throws IOException
	 */
	public Object deserialize(byte[] by) throws IOException {
		Object reObj;
		if (by == null)
			throw new NullPointerException();
		ByteArrayInputStream is = new ByteArrayInputStream(by);
		Hessian2Input h2i = new Hessian2Input(is);
		h2i.setSerializerFactory(factory);
		reObj = h2i.readObject();
		h2i.close();
		is.close();
		return reObj;
	}

	public static void main(String[] arg) {

		long begin = System.currentTimeMillis();
		HessianSerialize aHessianSerialize = new HessianSerialize();
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
				} catch (IOException e) {
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
