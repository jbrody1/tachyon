package tachyon.client;

import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import tachyon.LocalTachyonCluster;
import tachyon.TestUtils;
import tachyon.thrift.FileAlreadyExistException;
import tachyon.thrift.InvalidPathException;

/**
 * Unit tests for <code>tachyon.client.FileInStream</code>.
 */
public class FileInStreamTest {
  private LocalTachyonCluster mLocalTachyonCluster = null;
  private TachyonFS mTfs = null;

  @Before
  public final void before() throws IOException {
    System.setProperty("tachyon.user.quota.unit.bytes", "1000");
    System.setProperty("tachyon.user.default.block.size.byte", "50");
    mLocalTachyonCluster = new LocalTachyonCluster(10000);
    mLocalTachyonCluster.start();
    mTfs = mLocalTachyonCluster.getClient();
  }

  @After
  public final void after() throws Exception {
    mLocalTachyonCluster.stop();
    System.clearProperty("tachyon.user.quota.unit.bytes");
    System.clearProperty("tachyon.user.default.block.size.byte");
  }

  /**
   * Test <code>void read()</code>.
   */
  @Test
  public void readTest1() throws IOException, InvalidPathException, FileAlreadyExistException {
    for (int k = 100; k <= 200; k += 33) {
      for (WriteType op : WriteType.values()) {
        int fileId = TestUtils.createByteFile(mTfs, "/root/testFile_" + k + "_" + op, op, k);

        TachyonFile file = mTfs.getFile(fileId);
        InStream is = (k < 150 ?
            file.getInStream(ReadType.CACHE) : file.getInStream(ReadType.NO_CACHE));
        Assert.assertTrue(is instanceof FileInStream);
        byte[] ret = new byte[k];
        int value = is.read();
        int cnt = 0;
        while (value != -1) {
          ret[cnt ++] = (byte) value;
          value = is.read();
        }
        Assert.assertTrue(TestUtils.equalIncreasingByteArray(k, ret));
        is.close();

        is = (k < 150 ? file.getInStream(ReadType.CACHE) : file.getInStream(ReadType.NO_CACHE));
        Assert.assertTrue(is instanceof FileInStream);
        ret = new byte[k];
        value = is.read();
        cnt = 0;
        while (value != -1) {
          ret[cnt ++] = (byte) value;
          value = is.read();
        }
        Assert.assertTrue(TestUtils.equalIncreasingByteArray(k, ret));
        is.close();
      }
    }
  }

  /**
   * Test <code>void read(byte b[])</code>.
   */
  @Test
  public void readTest2() throws IOException, InvalidPathException, FileAlreadyExistException {
    for (int k = 100; k <= 300; k += 33) {
      for (WriteType op : WriteType.values()) {
        int fileId = TestUtils.createByteFile(mTfs, "/root/testFile_" + k + "_" + op, op, k);

        TachyonFile file = mTfs.getFile(fileId);
        InStream is = 
            (k < 200 ? file.getInStream(ReadType.CACHE) : file.getInStream(ReadType.NO_CACHE));
        Assert.assertTrue(is instanceof FileInStream);
        byte[] ret = new byte[k];
        Assert.assertEquals(k, is.read(ret));
        Assert.assertTrue(TestUtils.equalIncreasingByteArray(k, ret));
        is.close();

        is =  (k < 200 ? file.getInStream(ReadType.CACHE) : file.getInStream(ReadType.NO_CACHE));
        Assert.assertTrue(is instanceof FileInStream);
        ret = new byte[k];
        Assert.assertEquals(k, is.read(ret));
        Assert.assertTrue(TestUtils.equalIncreasingByteArray(k, ret));
        is.close();
      }
    }
  }

  /**
   * Test <code>void read(byte[] b, int off, int len)</code>.
   */
  @Test
  public void readTest3() throws IOException, InvalidPathException, FileAlreadyExistException {
    for (int k = 100; k <= 300; k += 33) {
      for (WriteType op : WriteType.values()) {
        int fileId = TestUtils.createByteFile(mTfs, "/root/testFile_" + k + "_" + op, op, k);

        TachyonFile file = mTfs.getFile(fileId);
        InStream is = 
            (k < 200 ? file.getInStream(ReadType.CACHE) : file.getInStream(ReadType.NO_CACHE));
        Assert.assertTrue(is instanceof FileInStream);
        byte[] ret = new byte[k / 2];
        Assert.assertEquals(k / 2, is.read(ret, 0, k / 2));
        Assert.assertTrue(TestUtils.equalIncreasingByteArray(k / 2, ret));
        is.close();

        is = (k < 200 ? file.getInStream(ReadType.CACHE) : file.getInStream(ReadType.NO_CACHE));
        Assert.assertTrue(is instanceof FileInStream);
        ret = new byte[k];
        Assert.assertEquals(k, is.read(ret, 0, k));
        Assert.assertTrue(TestUtils.equalIncreasingByteArray(k, ret));
        is.close();
      }
    }
  }
}
