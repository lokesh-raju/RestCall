/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iexceed.webcontainer.utils.hash;

import java.io.IOException;
import java.io.InputStream;

/**
 * Sha4J implements SHA-1, SHA-224, SHA-256, SHA-384 and SHA-512 algorithms.<br/>
 * 
 * <pre>
 * Copyright (C) 2006 Softabar
 * 
 * This program is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by the 
 * Free Software Foundation; either version 2 of the License, or 
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License 
 * along with this program; if not, write to the 
 * Free Software Foundation, * Inc., * 59 Temple Place, * Suite 330, 
 * Boston, MA 02111-1307 USA
 * </pre>
 * 
 * @version 1.0
 */
public class Sha4J {

	private static final int[] K256 = { 0x428a2f98, 0x71374491, 0xb5c0fbcf,
			0xe9b5dba5, 0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
			0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3, 0x72be5d74,
			0x80deb1fe, 0x9bdc06a7, 0xc19bf174, 0xe49b69c1, 0xefbe4786,
			0x0fc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc,
			0x76f988da, 0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7,
			0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967, 0x27b70a85,
			0x2e1b2138, 0x4d2c6dfc, 0x53380d13, 0x650a7354, 0x766a0abb,
			0x81c2c92e, 0x92722c85, 0xa2bfe8a1, 0xa81a664b, 0xc24b8b70,
			0xc76c51a3, 0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
			0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5, 0x391c0cb3,
			0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3, 0x748f82ee, 0x78a5636f,
			0x84c87814, 0x8cc70208, 0x90befffa, 0xa4506ceb, 0xbef9a3f7,
			0xc67178f2 };

	private static final long[] K512 = { 0x428a2f98d728ae22L,
			0x7137449123ef65cdL, 0xb5c0fbcfec4d3b2fL, 0xe9b5dba58189dbbcL,
			0x3956c25bf348b538L, 0x59f111f1b605d019L, 0x923f82a4af194f9bL,
			0xab1c5ed5da6d8118L, 0xd807aa98a3030242L, 0x12835b0145706fbeL,
			0x243185be4ee4b28cL, 0x550c7dc3d5ffb4e2L, 0x72be5d74f27b896fL,
			0x80deb1fe3b1696b1L, 0x9bdc06a725c71235L, 0xc19bf174cf692694L,
			0xe49b69c19ef14ad2L, 0xefbe4786384f25e3L, 0x0fc19dc68b8cd5b5L,
			0x240ca1cc77ac9c65L, 0x2de92c6f592b0275L, 0x4a7484aa6ea6e483L,
			0x5cb0a9dcbd41fbd4L, 0x76f988da831153b5L, 0x983e5152ee66dfabL,
			0xa831c66d2db43210L, 0xb00327c898fb213fL, 0xbf597fc7beef0ee4L,
			0xc6e00bf33da88fc2L, 0xd5a79147930aa725L, 0x06ca6351e003826fL,
			0x142929670a0e6e70L, 0x27b70a8546d22ffcL, 0x2e1b21385c26c926L,
			0x4d2c6dfc5ac42aedL, 0x53380d139d95b3dfL, 0x650a73548baf63deL,
			0x766a0abb3c77b2a8L, 0x81c2c92e47edaee6L, 0x92722c851482353bL,
			0xa2bfe8a14cf10364L, 0xa81a664bbc423001L, 0xc24b8b70d0f89791L,
			0xc76c51a30654be30L, 0xd192e819d6ef5218L, 0xd69906245565a910L,
			0xf40e35855771202aL, 0x106aa07032bbd1b8L, 0x19a4c116b8d2d0c8L,
			0x1e376c085141ab53L, 0x2748774cdf8eeb99L, 0x34b0bcb5e19b48a8L,
			0x391c0cb3c5c95a63L, 0x4ed8aa4ae3418acbL, 0x5b9cca4f7763e373L,
			0x682e6ff3d6b2b8a3L, 0x748f82ee5defb2fcL, 0x78a5636f43172f60L,
			0x84c87814a1f0ab72L, 0x8cc702081a6439ecL, 0x90befffa23631e28L,
			0xa4506cebde82bde9L, 0xbef9a3f7b2c67915L, 0xc67178f2e372532bL,
			0xca273eceea26619cL, 0xd186b8c721c0c207L, 0xeada7dd6cde0eb1eL,
			0xf57d4f7fee6ed178L, 0x06f067aa72176fbaL, 0x0a637dc5a2c898a6L,
			0x113f9804bef90daeL, 0x1b710b35131c471bL, 0x28db77f523047d84L,
			0x32caab7b40c72493L, 0x3c9ebe0a15c9bebcL, 0x431d67c49c100d4cL,
			0x4cc5d4becb3e42b6L, 0x597f299cfc657e2aL, 0x5fcb6fab3ad6faecL,
			0x6c44198c4a475817L };

	private boolean closeInputStream = true;
	// Start SHA-1 implementation
	// variables and functions also used in other SHA algorithms
	private long messageLength;
	private long messageLengthUpper;
	private long messageLengthLower;

	public Sha4J() {
		this(true);
	}

	public Sha4J(final boolean closeInpStrm) {
		this.closeInputStream = closeInpStrm;
	}

	public byte[] sha1Digest(InputStream inputStream)
			throws IOException {
		// read bytes
		 byte[] lBlock = new byte[64];// 512-bit block
		 int[] intBlock1 = new int[16];
		 int[] intBlock2 = new int[80];

		// SHA-1 constants, digest initial value
		// Hash values
		 int hexVal1 = 0x67452301;
		 int hexVal2 = 0xefcdab89;
		 int hexVal3 = 0x98badcfe;
		 int hexVal4 = 0x10325476;
		 int hexVal5 = 0xc3d2e1f0;

		 int[] hexArray = { hexVal1, hexVal2, hexVal3, hexVal4, hexVal5 };

		// read bytes from inputstream
		// and pad if necessary
		boolean padBlock = false;
		boolean padded = false;
		int bytesRead = inputStream.read(lBlock, 0, 64);
		for (;;) {
			if (bytesRead == -1) {
				if (!padded) {
					this.doPadBlock(hexArray, intBlock1, intBlock2, true);
				}

				final byte[] digest = new byte[20];
				int i = 0;
				int j = 0;

				for (; j < 5; i += 4, j++) {
					digest[i] = (byte) (hexArray[j] >>> 24);
					digest[i + 1] = (byte) (hexArray[j] >>> 16);
					digest[i + 2] = (byte) (hexArray[j] >>> 8);
					digest[i + 3] = (byte) (hexArray[j]);
				}
				if (this.closeInputStream) {
					inputStream.close();
				}

				return digest;
			}

			this.messageLength += bytesRead << 3;// messageLength in bits,

			if (bytesRead < 55) {
				padded = true;
				// do padding if read bytes is smaller than 55 ==> total message
				// length
				// can be added in this block as 64-bit value

				// add 1 after message
				lBlock[bytesRead] = (byte) 0x80;
				for (int i = bytesRead + 1; i < 56; i++) {
					lBlock[i] = 0;
				}
				// add length as 64 bit value at the end of Mb
				this.addLength128(lBlock);

			}

			if (bytesRead == 55) {
				padded = true;

				// need to add totally new 512 bit block with all zeros except
				// last 64 bits which is message length
				lBlock[55] = (byte) 0x80;
				this.addLength128(lBlock);

			}

			if (bytesRead > 55 && bytesRead < 64) {
				padded = true;

				// need to add totally new 512 bit block with all zeros except
				// last 64 bits which is message length
				lBlock[bytesRead] = (byte) 0x80;
				for (int i = bytesRead + 1; i < 64; i++) {
					lBlock[i] = 0;
				}
				padBlock = true;// padBlock is 448-bits of zero + 64 bit length
			}

			// convert to int array. Java int is 32-bits
			// int is a 32-bit word in FIPS 180-2 specification
			int i = 0;
			int j = 0;
			for (; i < 16; j += 4, i++) {
				intBlock1[i] = (lBlock[j] << 24)
						| (((lBlock[j + 1]) & 0xff) << 16)
						| (((lBlock[j + 2]) & 0xff) << 8)
						| ((lBlock[j + 3]) & 0xff);
			}

			// prepare message schedule
			this.prepareMessageSchedule(intBlock1, intBlock2);

			// init working variables
			int temp1 = hexArray[0];// 1732584193
			int temp2 = hexArray[1];// 4023233417
			int temp3 = hexArray[2];// 2562383102
			int temp4 = hexArray[3];// 271733878
			int temp5 = hexArray[4];// 3285377520

			int temp = 0;
			for (; temp < 80; temp++) {
				final int lTemp = this.modulo32Add(this.modulo32Add(
						this.modulo32Add(this.rotL(5, temp1),
								this.f(temp, temp2, temp3, temp4)),
						this.modulo32Add(temp5, this.fnK(temp))),
						intBlock2[temp]);
				temp5 = temp4;
				temp4 = temp3;
				temp3 = this.rotL(30, temp2);
				temp2 = temp1;
				temp1 = lTemp;

			}

			// interMediateHash128 of H,a,b,c,d,e
			hexArray[0] = this.modulo32Add(hexArray[0], temp1);
			hexArray[1] = this.modulo32Add(hexArray[1], temp2);
			hexArray[2] = this.modulo32Add(hexArray[2], temp3);
			hexArray[3] = this.modulo32Add(hexArray[3], temp4);
			hexArray[4] = this.modulo32Add(hexArray[4], temp5);

			// do last pad
			if (padBlock) {
				this.doPadBlock(hexArray, intBlock1, intBlock2, false);
			}

			bytesRead = inputStream.read(lBlock, 0, 64);
		}

	}

	private void addLength128(final byte[] byteArr) {
		byteArr[56] = (byte) (this.messageLength >>> 56);
		byteArr[57] = (byte) (this.messageLength >>> 48);
		byteArr[58] = (byte) (this.messageLength >>> 40);
		byteArr[59] = (byte) (this.messageLength >>> 32);
		byteArr[60] = (byte) (this.messageLength >>> 24);
		byteArr[61] = (byte) (this.messageLength >>> 16);
		byteArr[62] = (byte) (this.messageLength >>> 8);
		byteArr[63] = (byte) this.messageLength;
	}

	private void doPadBlock(final int[] param1, final int[] param2,
			final int[] param3, final boolean addOne) {

		// addOne is the required 1 before
		param2[0] = addOne ? 0x80000000 : 0;
		for (int i = 1; i < 14; i++) {
			param2[i] = 0;
		}

		param2[14] = (int) (this.messageLength >>> 32);
		param2[15] = (int) this.messageLength;

		// prepare message schedule
		this.prepareMessageSchedule(param2, param3);

		// init working variables
		int temp1 = param1[0];// 1732584193
		int temp2 = param1[1];// 4023233417
		int temp3 = param1[2];// 2562383102
		int temp4 = param1[3];// 271733878
		int temp5 = param1[4];// 3285377520

		int temp = 0;
		for (; temp < 80; temp++) {
			final int ltemp = this.modulo32Add(
					this.modulo32Add(
							this.modulo32Add(this.rotL(5, temp1),
									this.f(temp, temp2, temp3, temp4)),
							this.modulo32Add(temp5, this.fnK(temp))),
					param3[temp]);
			temp5 = temp4;
			temp4 = temp3;
			temp3 = this.rotL(30, temp2);
			temp2 = temp1;
			temp1 = ltemp;

		}
		param1[0] = this.modulo32Add(param1[0], temp1);
		param1[1] = this.modulo32Add(param1[1], temp2);
		param1[2] = this.modulo32Add(param1[2], temp3);
		param1[3] = this.modulo32Add(param1[3], temp4);
		param1[4] = this.modulo32Add(param1[4], temp5);

	}

	private void prepareMessageSchedule(final int[] param1, final int[] param2) {
		for (int t = 0; t < 80; t++) {
			if (t < 16) {
				param2[t] = param1[t];
			} else {
				param2[t] = this.rotL(1, param2[t - 3] ^ param2[t - 8]
						^ param2[t - 14] ^ param2[t - 16]);
			}

		}
	}

	private int modulo32Add(int param1, int param2) {
		return (param1 ^ 0x80000000) + (param2 ^ 0x80000000);
	}

	private int f(int param1, int param2, int param3,
			final int param4) {
		if (param1 < 20) {
			return (param2 & param3) ^ (~param2 & param4);// Ch(x, y, z)
		}
		if (param1 < 40) {
			return param2 ^ param3 ^ param4;// Parity(x, y, z)
		}
		if (param1 < 60) {
			return (param2 & param3) ^ (param2 & param4) ^ (param3 & param4);
		}

		return param2 ^ param3 ^ param4;

	}

	private int fnCh(int param1, int param2, int param3) {
		return (param1 & param2) ^ (~param1 & param3);
	}

	private int maj(int param1, int param2, int param3) {
		return (param1 & param2) ^ (param1 & param3) ^ (param2 & param3);
	}

	private int rotL(int param1, int param2) {
		return (param2 << param1) | (param2 >>> (32 - param1));
	}

	private int fnK(int param) {

		if (param < 20) {
			return 0x5a827999;
		}
		if (param < 40) {
			return 0x6ed9eba1;
		}
		if (param < 60) {
			return 0x8f1bbcdc;
		}

		return 0xca62c1d6;
	}

	// End SHA-1 implementation

	// Start SHA-224/SHA-256 implementation

	public byte[] sha224Digest(InputStream inputStream)
			throws IOException {
		return this.sha256Digest(inputStream, true);
	}

	public byte[] sha256Digest(InputStream inputStream)
			throws IOException {
		return this.sha256Digest(inputStream, false);
	}

	private byte[] sha256Digest(InputStream inputStream,
			boolean sha224) throws IOException {
		// read bytes
		 byte[] temp1 = new byte[64];// 512-bit block
		 int[] temp2 = new int[16];
		 int[] temp3 = new int[64];

		// SHA-256 constants, digest initial value

		int hexVar0 = 0x6a09e667;
		int hexVar1 = 0xbb67ae85;
		int hexVar2 = 0x3c6ef372;
		int hexVar3 = 0xa54ff53a;
		int hexVar4 = 0x510e527f;
		int hexVar5 = 0x9b05688c;
		int hexVar6 = 0x1f83d9ab;
		int hexVar7 = 0x5be0cd19;

		if (sha224) {
			hexVar0 = 0xc1059ed8;
			hexVar1 = 0x367cd507;
			hexVar2 = 0x3070dd17;
			hexVar3 = 0xf70e5939;
			hexVar4 = 0xffc00b31;
			hexVar5 = 0x68581511;
			hexVar6 = 0x64f98fa7;
			hexVar7 = 0xbefa4fa4;
		}

		final int[] h11 = { hexVar0, hexVar1, hexVar2, hexVar3, hexVar4,
				hexVar5, hexVar6, hexVar7 };

		// read bytes from inputstream
		// and pad if necessary
		boolean padBlock = false;
		boolean padded = false;
		int bytesRead = inputStream.read(temp1, 0, 64);
		for (;;) {
			if (bytesRead == -1) {
				if (!padded) {
					this.doPadBlock256(h11, temp2, temp3, true);
				}

				byte[] digest = new byte[32];
				int i = 0;
				int j = 0;
				for (; j < 8; i += 4, j++) {
					digest[i] = (byte) (h11[j] >>> 24);
					digest[i + 1] = (byte) (h11[j] >>> 16);
					digest[i + 2] = (byte) (h11[j] >>> 8);
					digest[i + 3] = (byte) (h11[j]);
				}
				if (this.closeInputStream) {
					inputStream.close();
				}

				if (sha224) {
					byte[] digest2 = new byte[28];
					for (int k = 0; k < digest2.length; k++) {
						digest2[k] = digest[k];
					}
					return digest2;
				}
				return digest;
			}

			this.messageLength += bytesRead << 3;// messageLength in bits,

			if (bytesRead < 55) {
				padded = true;
				// do padding if read bytes is smaller than 55 ==> total message
				// length
				// can be added in this block as 64-bit value

				// add 1 after message
				temp1[bytesRead] = (byte) 0x80;
				for (int i = bytesRead + 1; i < 56; i++) {
					temp1[i] = 0;
				}
				// add length as 64 bit value at the end of Mb
				this.addLength128(temp1);

			}

			if (bytesRead == 55) {
				padded = true;

				// need to add totally new 512 bit block with all zeros except
				// last 64 bits which is message length
				temp1[55] = (byte) 0x80;
				this.addLength128(temp1);

			}

			if (bytesRead > 55 && bytesRead < 64) {
				padded = true;

				// need to add totally new 512 bit block with all zeros except
				// last 64 bits which is message length
				temp1[bytesRead] = (byte) 0x80;
				for (int i = bytesRead + 1; i < 64; i++) {
					temp1[i] = 0;
				}
				padBlock = true;// padBlock is 448-bits of zero + 64 bit length
			}

			// convert to int array. Java int is 32-bits
			// int is a 32-bit word in FIPS 180-2 specification
			int i = 0;
			int j = 0;
			for (; i < 16; j += 4, i++) {
				temp2[i] = (temp1[j] << 24) | (((temp1[j + 1]) & 0xff) << 16)
						| (((temp1[j + 2]) & 0xff) << 8)
						| ((temp1[j + 3]) & 0xff);

			}

			// prepare message schedule
			this.prepareMessageSchedule256(temp2, temp3);

			// init working variables 1732584193, 4023233417, 2562383102, 271733878, 3285377520 
			int a = h11[0];
			int b = h11[1];
			int c = h11[2];
			int d = h11[3];
			int e = h11[4];
			int f = h11[5];
			int g = h11[6];
			int h = h11[7];

			int t = 0;
			for (; t < 64; t++) {
				 int t1 = this.modulo32Add(this.modulo32Add(
						this.modulo32Add(h, this.sIGMA2561(e)),
						this.modulo32Add(this.fnCh(e, f, g), K256[t])),
						temp3[t]);
				 int t2 = this.modulo32Add(this.sIGMA2560(a),
						this.maj(a, b, c));
				h = g;
				g = f;
				f = e;
				e = this.modulo32Add(d, t1);
				d = c;
				c = b;
				b = a;
				a = this.modulo32Add(t1, t2);
			}

			h11[0] = this.modulo32Add(h11[0], a);
			h11[1] = this.modulo32Add(h11[1], b);
			h11[2] = this.modulo32Add(h11[2], c);
			h11[3] = this.modulo32Add(h11[3], d);
			h11[4] = this.modulo32Add(h11[4], e);
			h11[5] = this.modulo32Add(h11[5], f);
			h11[6] = this.modulo32Add(h11[6], g);
			h11[7] = this.modulo32Add(h11[7], h);

			// do last pad
			if (padBlock) {
				this.doPadBlock256(h11, temp2, temp3, false);
			}

			bytesRead = inputStream.read(temp1, 0, 64);
		}

	}

	private void doPadBlock256(int[] H, int[] M, int[] W,
			boolean addOne) {
		// addOne is the required 1 before
		M[0] = addOne ? 0x80000000 : 0;
		for (int i = 1; i < 14; i++) {
			M[i] = 0;
		}

		M[14] = (int) (this.messageLength >>> 32);
		M[15] = (int) this.messageLength;

		// prepare message schedule
		this.prepareMessageSchedule256(M, W);

		// init working variables
		int a = H[0];// 1732584193
		int b = H[1];// 4023233417
		int c = H[2];// 2562383102
		int d = H[3];// 271733878
		int e = H[4];// 3285377520
		int f = H[5];// 3285377520
		int g = H[6];// 3285377520
		int h = H[7];// 3285377520

		int t = 0;
		int t1 = 0;
		int t2 = 0;
		for (; t < 64; t++) {
			t1 = this.modulo32Add(this.modulo32Add(
					this.modulo32Add(h, this.sIGMA2561(e)),
					this.modulo32Add(this.fnCh(e, f, g), K256[t])), W[t]);
			t2 = this.modulo32Add(this.sIGMA2560(a), this.maj(a, b, c));
			h = g;
			g = f;
			f = e;
			e = this.modulo32Add(d, t1);
			d = c;
			c = b;
			b = a;
			a = this.modulo32Add(t1, t2);
		}

		H[0] = this.modulo32Add(H[0], a);
		H[1] = this.modulo32Add(H[1], b);
		H[2] = this.modulo32Add(H[2], c);
		H[3] = this.modulo32Add(H[3], d);
		H[4] = this.modulo32Add(H[4], e);
		H[5] = this.modulo32Add(H[5], f);
		H[6] = this.modulo32Add(H[6], g);
		H[7] = this.modulo32Add(H[7], h);

	}

	private void prepareMessageSchedule256(int[] m, int[] w) {
		for (int t = 0; t < 64; t++) {

			if (t < 16) {
				w[t] = m[t];
			} else {
				w[t] = this.modulo32Add(
						this.modulo32Add(this.sigma2561(w[t - 2]), w[t - 7]),
						this.modulo32Add(this.sigma2560(w[t - 15]), w[t - 16]));
			}

		}

	}

	private int sIGMA2560(int x) {
		return rotr(2, x) ^ rotr(13, x) ^ rotr(22, x);

	}

	private int sIGMA2561(int x) {
		return rotr(6, x) ^ rotr(11, x) ^ rotr(25, x);

	}

	private int sigma2560(int x) {
		return rotr(7, x) ^ rotr(18, x) ^ shr(3, x);

	}

	private int sigma2561(int x) {
		return rotr(17, x) ^ rotr(19, x) ^ shr(10, x);

	}

	private int rotr(int n, int x) {
		return (x >>> n) | (x << (32 - n));

	}

	private int shr(int n, int x) {
		return x >>> n;

	}

	// End SHA-224/256 implementation

	// Start SHA-512/384 implementation

	// SHA512 max length is 128bits

	public byte[] sha384Digest(InputStream inputStream)
			throws IOException {
		return this.sha512Digest(inputStream, true);

	}

	public byte[] sha512Digest(InputStream inputStream)
			throws IOException {
		return this.sha512Digest(inputStream, false);
	}

	private byte[] sha512Digest(final InputStream inputStream,
			boolean sha384) throws IOException {

		// read bytes
		 byte[] mb = new byte[128];// 1024-bit block
		 long[] m = new long[16];
		 long[] w = new long[80];

		// SHA-512 constants, digest initial value
		long h0 = 0x6a09e667f3bcc908L;
		long h1 = 0xbb67ae8584caa73bL;
		long h2 = 0x3c6ef372fe94f82bL;
		long h3 = 0xa54ff53a5f1d36f1L;
		long h4 = 0x510e527fade682d1L;
		long h5 = 0x9b05688c2b3e6c1fL;
		long h6 = 0x1f83d9abfb41bd6bL;
		long h7 = 0x5be0cd19137e2179L;
		if (sha384) {
			h0 = 0xcbbb9d5dc1059ed8L;
			h1 = 0x629a292a367cd507L;
			h2 = 0x9159015a3070dd17L;
			h3 = 0x152fecd8f70e5939L;
			h4 = 0x67332667ffc00b31L;
			h5 = 0x8eb44a8768581511L;
			h6 = 0xdb0c2e0d64f98fa7L;
			h7 = 0x47b5481dbefa4fa4L;
		}

		final long[] h11 = { h0, h1, h2, h3, h4, h5, h6, h7 };

		// read bytes from inputstream
		// and pad if necessary
		boolean padBlock = false;
		boolean padded = false;
		int bytesRead = inputStream.read(mb, 0, 128);
		for (;;) {
			if (bytesRead == -1) {
				if (!padded) {
					this.doPadBlock512(h11, m, w, true);
				}

				final byte[] digest = new byte[64];
				int i = 0;
				int j = 0;
				for (; j < 8; i += 8, j++) {
					digest[i] = (byte) (h11[j] >>> 56);
					digest[i + 1] = (byte) (h11[j] >>> 48);
					digest[i + 2] = (byte) (h11[j] >>> 40);
					digest[i + 3] = (byte) (h11[j] >>> 32);
					digest[i + 4] = (byte) (h11[j] >>> 24);
					digest[i + 5] = (byte) (h11[j] >>> 16);
					digest[i + 6] = (byte) (h11[j] >>> 8);
					digest[i + 7] = (byte) (h11[j]);

				}
				if (this.closeInputStream) {
					inputStream.close();
				}

				if (sha384) {
					 byte[] digest2 = new byte[48];
					for (int k = 0; k < digest2.length; k++) {
						digest2[k] = digest[k];
					}
					return digest2;
				}

				return digest;
			}

			this.messageLengthLower += bytesRead << 3;// messageLength in bits,

			if (bytesRead < 110) {
				padded = true;
				// do padding if read bytes is smaller than 55 ==> total message
				// length
				// can be added in this block as 64-bit value
				// add 1 after message
				mb[bytesRead] = (byte) 0x80;
				for (int i = bytesRead + 1; i < 112; i++) {
					mb[i] = 0;
				}
				// add length as 128 bit value at the end of Mb
				this.addLength512(mb);
			}

			if (bytesRead == 110) {
				padded = true;

				// need to add totally new 512 bit block with all zeros except
				// last 64 bits which is message length
				mb[110] = (byte) 0x80;
				this.addLength512(mb);
			}

			if (bytesRead > 110 && bytesRead < 128) {
				padded = true;

				// need to add totally new 512 bit block with all zeros except
				// last 64 bits which is message length
				mb[bytesRead] = (byte) 0x80;
				for (int i = bytesRead + 1; i < 128; i++) {
					mb[i] = 0;
				}
				padBlock = true;// padBlock is 896-bits of zero + 128 bit length
			}

			// convert to long array. Java long is 64-bits
			int i = 0;
			int j = 0;
			for (; i < 16; j += 8, i++) {

				m[i] = (((long) mb[j]) << 56)
						| ((long) ((mb[j + 1]) & 0xff) << 48)
						| ((long) ((mb[j + 2]) & 0xff) << 40)
						| ((long) ((mb[j + 3]) & 0xff) << 32)
						| ((long) ((mb[j + 4]) & 0xff) << 24)
						| ((long) ((mb[j + 5]) & 0xff) << 16)
						| ((long) ((mb[j + 6]) & 0xff) << 8)
						| ((long) (mb[j + 7]) & 0xff);
			}

			// prepare message schedule
			this.prepareMessageSchedule512(m, w);
			// init working variables
			long a = h11[0];// 1732584193
			long b = h11[1];// 4023233417
			long c = h11[2];// 2562383102
			long d = h11[3];// 271733878
			long e = h11[4];// 3285377520
			long f = h11[5];// 3285377520
			long g = h11[6];// 3285377520
			long h = h11[7];// 3285377520

			int t = 0;
			for (; t < 80; t++) {
				 long t1 = this.modulo64Add(this.modulo64Add(h,
						this.sIGMA5121(e)), this.modulo64Add(
						this.modulo64Add(this.ch(e, f, g), K512[t]), w[t]));

				 long t2 = this.modulo64Add(this.sIGMA5120(a),
						this.maj1(a, b, c));

				h = g;
				g = f;
				f = e;
				e = this.modulo64Add(d, t1);
				d = c;
				c = b;
				b = a;
				a = this.modulo64Add(t1, t2);

			}

			h11[0] = this.modulo64Add(h11[0], a);
			h11[1] = this.modulo64Add(h11[1], b);
			h11[2] = this.modulo64Add(h11[2], c);
			h11[3] = this.modulo64Add(h11[3], d);
			h11[4] = this.modulo64Add(h11[4], e);
			h11[5] = this.modulo64Add(h11[5], f);
			h11[6] = this.modulo64Add(h11[6], g);
			h11[7] = this.modulo64Add(h11[7], h);

			// do last pad
			if (padBlock) {
				this.doPadBlock512(h11, m, w, false);
			}

			bytesRead = inputStream.read(mb, 0, 128);
		}

	}

	private void doPadBlock512(long[] lH, long[] lM,
			long[] lW, final boolean addOne) {
		// addOne is the required 1 before
		lM[0] = addOne ? 0x8000000000000000L : 0;
		for (int i = 1; i < 14; i++) {
			lM[i] = 0;
		}

		lM[14] = this.messageLengthUpper;
		lM[15] = this.messageLengthLower;

		// prepare message schedule
		this.prepareMessageSchedule512(lM, lW);

		// init working variables
		long a = lH[0];// 1732584193
		long b = lH[1];// 4023233417
		long c = lH[2];// 2562383102
		long d = lH[3];// 271733878
		long e = lH[4];// 3285377520
		long f = lH[5];// 3285377520
		long g = lH[6];// 3285377520
		long h = lH[7];// 3285377520

		int t = 0;
		long t1 = 0;
		long t2 = 0;
		for (; t < 80; t++) {
			t1 = this.modulo64Add(
					this.modulo64Add(this.modulo64Add(
							this.modulo64Add(h, this.sIGMA5121(e)),
							this.ch(e, f, g)), K512[t]), lW[t]);
			t2 = this.modulo64Add(this.sIGMA5120(a), this.maj1(a, b, c));
			h = g;
			g = f;
			f = e;
			e = this.modulo64Add(d, t1);
			d = c;
			c = b;
			b = a;
			a = this.modulo64Add(t1, t2);

		}

		lH[0] = this.modulo64Add(lH[0], a);
		lH[1] = this.modulo64Add(lH[1], b);
		lH[2] = this.modulo64Add(lH[2], c);
		lH[3] = this.modulo64Add(lH[3], d);
		lH[4] = this.modulo64Add(lH[4], e);
		lH[5] = this.modulo64Add(lH[5], f);
		lH[6] = this.modulo64Add(lH[6], g);
		lH[7] = this.modulo64Add(lH[7], h);

	}

	private long ch(long x, long y, long z) {
		return (x & y) ^ (~x & z);

	}

	private long maj1(long x, long y, long z) {
		return (x & y) ^ (x & z) ^ (y & z);
	}

	private long sIGMA5120(long x) {
		return rOTR(28, x) ^ rOTR(34, x) ^ rOTR(39, x);

	}

	private long sIGMA5121(long x) {
		return rOTR(14, x) ^ rOTR(18, x) ^ rOTR(41, x);

	}

	private void prepareMessageSchedule512(long[] lM, long[] lW) {
		for (int t = 0; t < 80; t++) {
			if (t < 16) {
				lW[t] = lM[t];
			} else {

				lW[t] = this.modulo64Add(this.modulo64Add(
						this.sigma5121(lW[t - 2]), lW[t - 7]), this
						.modulo64Add(this.sigma5120(lW[t - 15]), lW[t - 16]));

			}
		}
	}

	private long sigma5120(long x) {
		return this.rOTR(1, x) ^ rOTR(8, x) ^ sHR(7, x);
	}

	private long sigma5121(long x) {
		return this.rOTR(19, x) ^ rOTR(61, x) ^ sHR(6, x);
	}

	private long rOTR(int n, long x) {
		return (x >>> n) | (x << (64 - n));

	}

	private long sHR(int n, long x) {
		return x >>> n;

	}

	private long modulo64Add(long x, long y) {
		return (x ^ 0x8000000000000000L) + (y ^ 0x8000000000000000L);

	}

	private void addLength512(byte[] Mb) {
		Mb[112] = (byte) (this.messageLengthUpper >>> 56);
		Mb[113] = (byte) (this.messageLengthUpper >>> 48);
		Mb[114] = (byte) (this.messageLengthUpper >>> 40);
		Mb[115] = (byte) (this.messageLengthUpper >>> 32);
		Mb[116] = (byte) (this.messageLengthUpper >>> 24);
		Mb[117] = (byte) (this.messageLengthUpper >>> 16);
		Mb[118] = (byte) (this.messageLengthUpper >>> 8);
		Mb[119] = (byte) this.messageLengthUpper;

		Mb[120] = (byte) (this.messageLengthLower >>> 56);
		Mb[121] = (byte) (this.messageLengthLower >>> 48);
		Mb[122] = (byte) (this.messageLengthLower >>> 40);
		Mb[123] = (byte) (this.messageLengthLower >>> 32);
		Mb[124] = (byte) (this.messageLengthLower >>> 24);
		Mb[125] = (byte) (this.messageLengthLower >>> 16);
		Mb[126] = (byte) (this.messageLengthLower >>> 8);
		Mb[127] = (byte) this.messageLengthLower;

	}

	// ENd SHA-512 implementation

	public final void reset() {
		// sha-1 variables
		this.messageLength = 0;

		// sha-512 variables
		this.messageLengthUpper = 0L;
		this.messageLengthLower = 0L;

	}
}
