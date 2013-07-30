package org.jfree.graphics2d;

/*
 * Copyright (c) 2009-2013, i Data Connect!
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */


import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * <p>
 * An ascii85 encoder, implemented as an {@link OutputStream}.
 * </p>
 * <p>
 * Call <code>flush()</code> or <code>close()</code> to properly close the
 * ascii85 block. The block must be closed for the encoded data to be valid.
 * Do not call <code>flush()</code> before you intend to end the ascii85 block.
 * Multiple ascii85 blocks may be encoded by calling flush() and then writing
 * more bytes to the stream.
 * </p>
 * <p>
 * Note that if you use the constructor with the
 * <code>useSpaceCompression</code> option, the encoded text will be shorter
 * when there are many consecutive space characters in the encoded data, but
 * it will not be compatible with Adobe's ascii85 implementation. It makes sense
 * to use this option if interoperability with other ascii85 implementations
 * is not a requirement.
 * </p>
 * @author Ben Upsavs
 */
public class Ascii85OutputStream extends FilterOutputStream {

    private int width = 72;
    private int pos;
    private int tuple;
    private int count;
    private boolean encoding;
    private boolean useSpaceCompression;

    /**
     * Creates an output stream to encode ascii85 data, using a default line
     * with of 72 characters and not using the space character compression
     * option. Call <code>flush()</code> to add the padding and end the ascii85
     * block.
     */
    public Ascii85OutputStream(OutputStream out) {
        super(out);
    }

    /**
     * Creates an output stream to encode ascii85 data, using a default line
     * width of 72 characters. Call <code>flush()</code> to end the ascii85
     * block.
     * @param useSpaceCompression Whether to use space character compression in
     * the output.
     */
    public Ascii85OutputStream(OutputStream out, boolean useSpaceCompression) {
        this(out);
        this.useSpaceCompression = useSpaceCompression;
    }

    /**
     * Creates an output stream to encode ascii85 data. Call
     * <code>flush()</code> to end the ascii85 block.
     * @param width The maximum line width of the encoded output text.
     * Whitespace characters are ignored when decoding.
     * @param useSpaceCompression Whether to use space character compression in
     * the output.
     */
    public Ascii85OutputStream(OutputStream out, int width, boolean useSpaceCompression) {
        this(out);
        this.width = width;
        this.useSpaceCompression = useSpaceCompression;
    }

    private void startEncoding() throws IOException {
        //out.write('<');
        //out.write('~');
        //pos = 2;
        encoding = true;
    }

    /**
     * Writes a single byte to the stream. See
     * {@link OutputStream#write(int b)} for details.
     * @param b The byte to encode.
     * @throws java.io.IOException If an I/O error occurs in the underlying
     * output stream.
     */
    public void write(int b) throws IOException {
        if (!encoding)
            startEncoding();

        switch (count++) {
            case 0:
                tuple |= ((b & 0xff) << 24);
                break;
            case 1:
                tuple |= ((b & 0xff) << 16);
                break;
            case 2:
                tuple |= ((b & 0xff) << 8);
                break;
            case 3:
                tuple |= (b & 0xff);
                if (tuple == 0) {
                    // Use null compression
                    out.write('z');
                    if (pos++ >= width) {
                        pos = 0;
                        out.write('\r');
                        out.write('\n');
                    }
                } else if (useSpaceCompression && (tuple == 0x20202020)) {
                    // Use space compression
                    out.write('y');
                    if (pos++ >= width) {
                        pos = 0;
                        out.write('\r');
                        out.write('\n');
                    }
                } else
                    encode(tuple, count);

                tuple = 0;
                count = 0;
                break;
        }
    }

    /**
     * Writes a single byte to the underlying output stream, unencoded. If
     * done improperly, this may corrupt the ascii85 data stream. Writing
     * a byte using this method may cause the line length to increase since
     * the line length counter will not be updated by this method.
     * @param b The byte to write.
     * @throws java.io.IOException If the underlying output stream has an I/O
     * error.
     */
    public void writeUnencoded(int b) throws IOException {
        super.write(b);
    }

    /**
     * Writes bytes to the underlying output stream, unencoded. If done
     * improperly, this may corrupt the ascii85 data stream. Writing bytes
     * using this method may cause the line length to increase since the line
     * length counter will not be updated by this method.
     * @param b The bytes to write.
     * @throws java.io.IOException If the underlying output stream has an I/O
     * error.
     */
    public void writeUnencoded(byte[] b) throws IOException {
        writeUnencoded(b, 0, b.length);
    }

    /**
     * Writes bytes to the underlying output stream, unencoded. If done
     * improperly, this may corrupt the ascii85 data stream. Writing bytes
     * using this method may cause the line length to increase since the line
     * length counter will not be updated by this method.
     * @param b The bytes to write.
     * @param off The offset of <code>b</code> to start reading from.
     * @param len The amount of bytes to read from <code>b</code>.
     * @throws java.io.IOException If the underlying output stream has an I/O
     * error.
     */
    public void writeUnencoded(byte[] b, int off, int len) throws IOException {
        for (int i = 0; i < len; i++)
            writeUnencoded(b[off + i]);
    }

    /**
     * Encodes <code>tuple</code> and writes it to the output stream. The number
     * of bytes in the tuple, and thus the value of <code>count</code> is
     * normally 4, however less bytes may also be encoded, particularly if the
     * input stream has ended before the current tuple is full.
     * @param tuple The tuple to encode.
     * @param count The number of bytes stuffed into the tuple.
     * @throws IOException If an I/O error occurs.
     */
    private void encode(int tuple, int count) throws IOException {
        int i = 5;
        byte[] buf = new byte[5];
        short bufPos = 0;

        long longTuple = (tuple & 0xffffffffL);

        do {
            buf[bufPos++] = (byte)(longTuple % 85);
            longTuple /= 85;
        } while (--i > 0);

        i = count;
        do {
            out.write(buf[--bufPos] + '!');
            if (pos++ >= width) {
                pos = 0;
                out.write('\r');
                out.write('\n');
            }
        } while (i-- > 0);
    }

    /**
     * Adds the closing block and flushes the underlying output stream. This
     * method should only be called if it is intended that the ascii85 block
     * should be closed.
     */
    public void flush() throws IOException {
        // Add padding if required.
        if (encoding) {
            if (count > 0)
                encode(tuple, count);
            if (pos + 2 > width) {
                out.write('\r');
                out.write('\n');
            }

            out.write('~');
            out.write('>');
            out.write('\r');
            out.write('\n');

            encoding = false;
            tuple = count = 0;
        }

        super.flush();
    }
}
