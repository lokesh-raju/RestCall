package com.iexceed.appzillon.securityutils;

import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.logging.Logger;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;

import java.io.*;

/**
 * Created by diganta.kumar@i-exceed.com on 26/2/18 11:35 AM
 */
public class PemFile {

    private PemObject pemObject;

    public PemFile(String filename) throws FileNotFoundException, IOException {
        PemReader pemReader =null;
        if(Utils.isNullOrEmpty(Logger.propertiesPath)) {
            pemReader = new PemReader(new InputStreamReader(PemFile.class.getClassLoader().getResourceAsStream(filename)));
        }else {
            pemReader = new PemReader(new InputStreamReader(PemFile.class.getClassLoader().getResourceAsStream(Logger.propertiesPath+"/"+filename)));
        }
        try {
            this.pemObject = pemReader.readPemObject();
        } finally {
            pemReader.close();
        }
    }

    public void write(String filename) throws FileNotFoundException,
            IOException {
        PemWriter pemWriter = new PemWriter(new OutputStreamWriter(
                new FileOutputStream(filename)));
        try {
            pemWriter.writeObject(this.pemObject);
        } finally {
            pemWriter.close();
        }
    }

    public PemObject getPemObject() {
        return pemObject;
    }

}
