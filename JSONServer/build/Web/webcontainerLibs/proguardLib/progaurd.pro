-injars ../../target/appzillonplugins.jar
-outjars ../../ObfuscatedJar

-libraryjars <java.home>/lib/rt.jar
-libraryjars <java.home>/lib/jce.jar
-libraryjars <user.home>/.m2/repository/commons-codec/commons-codec/1.5/commons-codec-1.5.jar
-libraryjars <user.home>/.m2/repository/javax/servlet/javax.servlet-api/3.0.1/javax.servlet-api-3.0.1.jar
-libraryjars <user.home>/.m2/repository/commons-io/commons-io/2.4/commons-io-2.4.jar
-libraryjars <user.home>/.m2/repository/org/apache/logging/log4j/log4j-core/2.7/log4j-core-2.7.jar
-libraryjars <user.home>/.m2/repository/org/apache/logging/log4j/log4j-api/2.7/log4j-api-2.7.jar
#-libraryjars <user.home>/.m2/repository/org/twitter4j/twitter4j-core/4.0.3/twitter4j-core-4.0.3.jar
-libraryjars <user.home>/.m2/repository/org/springframework/spring-expression/4.0.5.RELEASE/spring-expression-4.0.5.RELEASE.jar
-libraryjars <user.home>/.m2/repository/org/springframework/spring-core/4.0.5.RELEASE/spring-core-4.0.5.RELEASE.jar
-libraryjars <user.home>/.m2/repository/org/springframework/spring-context/4.0.5.RELEASE/spring-context-4.0.5.RELEASE.jar
-libraryjars <user.home>/.m2/repository/org/springframework/spring-beans/4.0.5.RELEASE/spring-beans-4.0.5.RELEASE.jar
-libraryjars <user.home>/.m2/repository/org/springframework/spring-aop/4.0.5.RELEASE/spring-aop-4.0.5.RELEASE.jar
-libraryjars <user.home>/.m2/repository/commons-logging/commons-logging/1.1.3/commons-logging-1.1.3.jar
-libraryjars <user.home>/.m2/repository/aopalliance/aopalliance/1.0/aopalliance-1.0.jar
-libraryjars <user.home>/.m2/repository/org/apache/httpcomponents/httpmime/4.3.6/httpmime-4.3.6.jar
-libraryjars <user.home>/.m2/repository/org/apache/httpcomponents/httpclient/4.3.4/httpclient-4.3.4.jar
-libraryjars <user.home>/.m2/repository/org/apache/httpcomponents/httpcore/4.4.5/httpcore-4.4.5.jar
-libraryjars <user.home>/.m2/repository/commons-fileupload/commons-fileupload/1.2/commons-fileupload-1.2.jar
-libraryjars <user.home>/.m2/repository/com/sun/jersey/jersey-client/1.18.1/jersey-client-1.18.1.jar
-libraryjars <user.home>/.m2/repository/com/sun/jersey/contribs/jersey-multipart/1.18.1/jersey-multipart-1.18.1.jar
-libraryjars <user.home>/.m2/repository/com/sun/jersey/jersey-core/1.18.1/jersey-core-1.18.1.jar
-libraryjars <user.home>/.m2/repository/com/sun/jersey/jersey-json/1.18.1/jersey-json-1.18.1.jar
-libraryjars <user.home>/.m2/repository/org/codehaus/jettison/jettison/1.1/jettison-1.1.jar
-libraryjars <user.home>/.m2/repository/org/owasp/csrfguard/3.1.0/csrfguard-3.1.0.jar
-libraryjars <user.home>/.m2/repository/org/json/json/20090211/json-20090211.jar
-libraryjars <user.home>/.m2/repository/org/bouncycastle/bcprov-jdk16/1.46/bcprov-jdk16-1.46.jar
-dontskipnonpubliclibraryclassmembers
-dontshrink
-dontoptimize
-useuniqueclassmembernames

# Save the obfuscation mapping to a file, so you can de-obfuscate any stack
# traces later on. Keep a fixed source file attribute and all line number
# tables to get line numbers in the stack traces.
# You can comment this out if you're not interested in stack traces.

-printmapping ../Obfuscation.log
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

# Preserve all annotations.

-keepattributes *Annotation*

# You can print out the seeds that are matching the keep options below.

#-printseeds out.seeds

# Preserve all public servlets.
-keep public class com.iexceed.webcontainer.startup.WebContextListener
-keep public class com.iexceed.webcontainer.utils.AppzillonRequestFilter
-keep public class com.iexceed.webcontainer.servlet.AppzillonWebContainer
-keep public class com.iexceed.webcontainer.servlet.AppzillonMaps
-keep public class com.iexceed.webcontainer.utils.CsrfGuardFilter
-keep public class com.iexceed.webcontainer.utils.CsrfRedirect

-keep public class com.iexceed.webcontainer.utils.PropertyUtils {
  <methods>; 
}

-keep interface com.iexceed.webcontainer.utils.AppzillonConstants { *; }

-keep interface com.iexceed.appzillon.custom.IRequestProcessor {
    <methods>;
}
-keep public class com.iexceed.appzillon.custom.WebRequestProcessorImpl

-keep public class com.iexceed.webcontainer.logger.**{ 
 <methods>; 
 }

-keep public class com.iexceed.webcontainer.utils.WebProperties{ 
 <methods>; 
 }
 
-keep public class com.iexceed.webcontainer.utils.RSACryptoUtils {
  <methods>; 
}

# Preserve all native method names and the names of their classes.

# Preserve the special static methods that are required in all enumeration
# classes.

-keepclassmembers,allowoptimization enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Explicitly preserve all serialization members. The Serializable interface
# is only a marker interface, so it wouldn't save them.
# You can comment this out if your library doesn't use serialization.
# If your code contains serializable classes that have to be backward
# compatible, please refer to the manual.

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Your application may contain more items that need to be preserved;
# typically classes that are dynamically created using Class.forName:

# -keep public class mypackage.MyClass
# -keep public interface mypackage.MyInterface
# -keep public class * implements mypackage.MyInterface
