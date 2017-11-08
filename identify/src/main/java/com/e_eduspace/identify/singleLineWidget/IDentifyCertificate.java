package com.e_eduspace.identify.singleLineWidget;

/**
 * The <code>MyCertificate</code> class provides the bytes of the user
 * certificate used to grant the access to the MyScript technologies.
 */
public final class IDentifyCertificate
{
  /**
   * Returns the bytes of the user certificate.
   *
   * @return The bytes of the user certificate.
   */
  public static final byte[] getBytes()
  {
    return BYTES;
  }

  public static void setBytes(byte[] bytes){
    BYTES = bytes;
  }

  /**
   * The bytes of the user certificate.
   */
  private static byte[] BYTES;

} // end of: class MyCertificate
