/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.takes.f.auth;

import java.io.IOException;
import lombok.EqualsAndHashCode;

/**
 * XOR codec.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(of = { "origin", "secret" })
public final class CcXOR implements Codec {

    /**
     * Original codec.
     */
    private final transient Codec origin;

    /**
     * Secret to use for encoding.
     */
    private final transient byte[] secret;

    /**
     * Ctor.
     * @param codec Original codec
     * @param key Secret key for encoding
     */
    public CcXOR(final Codec codec, final String key) {
        this(codec, key.getBytes());
    }

    /**
     * Ctor.
     * @param codec Original codec
     * @param key Secret key for encoding
     */
    public CcXOR(final Codec codec, final byte[] key) {
        this.origin = codec;
        this.secret = new byte[key.length];
        System.arraycopy(key, 0, this.secret, 0, key.length);
    }

    @Override
    public String encode(final Identity identity) throws IOException {
        return new String(
            this.xor(
                this.origin.encode(identity).getBytes()
            )
        );
    }

    @Override
    public Identity decode(final String text) throws IOException {
        return this.origin.decode(
            new String(this.xor(text.getBytes()))
        );
    }

    /**
     * XOR array of bytes.
     * @param input The input to XOR
     * @return Encrypted output
     */
    private byte[] xor(final byte[] input) {
        final byte[] output = new byte[input.length];
        if (this.secret.length == 0) {
            System.arraycopy(input, 0, output, 0, input.length);
        } else {
            int spos = 0;
            for (int pos = 0; pos < input.length; ++pos) {
                output[pos] = (byte) (input[pos] ^ this.secret[spos]);
                ++spos;
                if (spos >= this.secret.length) {
                    spos = 0;
                }
            }
        }
        return output;
    }

}
