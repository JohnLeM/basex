package org.basex.io;

import static org.basex.util.Token.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Arrays;
import java.util.zip.ZipInputStream;

import org.basex.util.TokenBuilder;

/**
 * This class serves as a buffered wrapper for input streams.
 *
 * @author Workgroup DBIS, University of Konstanz 2005-09, ISC License
 * @author Christian Gruen
 */
public class BufferInput {
  /** Byte buffer. */
  protected byte[] buffer;
  /** Current buffer position. */
  protected int pos;
  /** Input length. */
  protected long length;
  /** Current buffer size. */
  private int size;
  /** Number of read bytes. */
  private int len;
  /** Reference to the data input stream. */
  private InputStream in;
  /** Default encoding for text files. */
  private String enc = UTF8;
  /** Charset decoder. */
  private CharsetDecoder csd;

  /**
   * Empty constructor.
   */
  protected BufferInput() { }

  /**
   * Fills the specified array with the beginning of the specified file.
   * @param file the file to be read
   * @param cont byte array
   * @throws IOException IO Exception
   */
  public static void read(final File file, final byte[] cont)
      throws IOException {
    new BufferInput(file, cont).close();
  }

  /**
   * Initializes the file reader.
   * @param file the file to be read
   * @throws IOException IO Exception
   */
  public BufferInput(final String file) throws IOException {
    this(new File(file));
  }

  /**
   * Initializes the file reader.
   * @param file the file to be read
   * @throws IOException IO Exception
   */
  public BufferInput(final File file) throws IOException {
    this(new FileInputStream(file));
    length = file.length();
  }

  /**
   * Initializes the file reader.
   * @param is input stream
   * @throws IOException IO Exception
   */
  public BufferInput(final InputStream is) throws IOException {
    this(is, new byte[IO.BLOCKSIZE]);
  }

  /**
   * Initializes the file reader.
   * @param file the file to be read
   * @param buf input buffer
   * @throws IOException IO Exception
   */
  public BufferInput(final File file, final byte[] buf) throws IOException {
    this(new FileInputStream(file), buf);
  }

  /**
   * Initializes the file reader.
   * @param is input stream
   * @param b input buffer
   * @throws IOException IO Exception
   */
  public BufferInput(final InputStream is, final byte[] b) throws IOException {
    this(b);
    in = is;
    next();
  }

  /**
   * Empty constructor.
   * @param buf buffer
   */
  public BufferInput(final byte[] buf) {
    buffer = buf;
    length = buf.length;
  }

  /**
   * Determines the file encoding.
   */
  public final void encoding() {
    final byte a = length > 0 ? buffer[0] : 0;
    final byte b = length > 1 ? buffer[1] : 0;
    final byte c = length > 2 ? buffer[2] : 0;
    final byte d = length > 3 ? buffer[3] : 0;
    if(a == -1 && b == -2 || a == '<' && b == 0 && c == '?' && d == 0) {
      enc = UTF16LE;
      if(a == -1) pos = 2;
    } else if(a == -2 && b == -1 || a == 0 && b == '<' && c == 0 && d == '?') {
      enc = UTF16BE;
      if(a == -2) pos = 2;
    } else if(a == -0x11 && b == -0x45 && c == -0x41) {
      pos = 3;
    }
  }

  /**
   * Sets a new encoding.
   * @param e encoding
   * @throws IOException IO Exception
   */
  public final void encoding(final String e) throws IOException {
    try {
      if(e.equals(UTF8) || e.equals(UTF82)) enc = UTF8;
      else if(e.equals(UTF16BE)) enc = UTF16BE;
      else if(e.equals(UTF16LE)) enc = UTF16LE;
      else enc = e;
      csd = Charset.forName(e).newDecoder();
    } catch(final Exception ex) {
      throw new IOException(ex.toString());
    }
  }

  /**
   * Reads a single byte and returns it as integer.
   * @return read byte
   * @throws IOException I/O exception
   */
  public final int read() throws IOException {
    return readByte() & 0xFF;
  }

  /**
   * Returns the next byte or 0 if all bytes have been read.
   * @return next byte
   * @throws IOException I/O exception
   */
  public byte readByte() throws IOException {
    if(pos >= size) {
      next();
      if(size <= 0) return 0;
    }
    return buffer[pos++];
  }

  /**
   * Reads a string from the input stream.
   * @return string
   * @throws IOException IO Exception
   */
  public String readString() throws IOException {
    final TokenBuilder tb = new TokenBuilder();
    byte l;
    while((l = readByte()) != 0) tb.add(l);
    return tb.toString();
  }

  /**
   * Reads the next buffer entry.
   * @throws IOException I/O exception
   */
  protected final void next() throws IOException {
    pos = 0;
    len += size;
    size = in.read(buffer);
  }

  /**
   * Returns the next character, 0 if all bytes have been read or
   * a negative character value -1 if the read byte is invalid.
   * @return next character
   * @throws IOException I/O exception
   */
  public final int readChar() throws IOException {
    // support encodings..
    byte ch = readByte();
    // comparison by references
    if(enc == UTF16LE) return ch & 0xFF | (readByte() & 0xFF) << 8;
    if(enc == UTF16BE) return (ch & 0xFF) << 8 | readByte() & 0xFF;
    if(enc == UTF8) {
      final int cl = cl(ch);
      if(cl == 1) return ch & 0xFF;
      CACHE[0] = ch;
      for(int c = 1; c < cl; c++) CACHE[c] = readByte();
      return cp(CACHE, 0);
    }
    if(ch >= 0) return ch;

    // convert other encodings.. loop until all needed bytes have been read
    int p = 0;
    while(true) {
      if(p == 4) return -CACHE[0];
      CACHE[p++] = ch;
      try {
        final CharBuffer cb = csd.decode(
            ByteBuffer.wrap(Arrays.copyOf(CACHE, p)));
        int i = 0;
        for(int c = 0; c < cb.limit(); c++) i |= cb.get(c) << (c << 3);
        return i;
      } catch(final CharacterCodingException ex) {
        ch = readByte();
      }
    }
  }

  /** UTF8 cache. */
  private static final byte[] CACHE = new byte[4];

  /**
   * Closes the input stream.
   * @throws IOException IO Exception
   */
  public final void close() throws IOException {
    if(in != null && !(in instanceof ZipInputStream)) in.close();
  }

  /**
   * Number of read bytes.
   * @return read bytes
   */
  public final int size() {
    return len + pos;
  }

  /**
   * Length of input.
   * @return read bytes
   */
  public final long length() {
    return length;
  }

  /**
   * Sets the input length.
   * @param l input length
   */
  public final void length(final long l) {
    length = l;
  }
}
