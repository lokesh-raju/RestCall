package com.iexceed.webcontainer.utils;

import com.iexceed.webcontainer.startup.WebContextListener;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;

import java.io.*;

public class PemFile {

  private PemObject pemObject;

  public PemFile(String filename) throws FileNotFoundException, IOException {
    PemReader pemReader = null;
    if(WebContextListener.propertiesPath !=null && !"".equals(WebContextListener.propertiesPath)) {
      pemReader = new PemReader(new InputStreamReader(PemFile.class.getClassLoader().getResourceAsStream(WebContextListener.propertiesPath+"/"+filename)));
    }else {
      pemReader = new PemReader(new InputStreamReader(PemFile.class.getClassLoader().getResourceAsStream(filename)));
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