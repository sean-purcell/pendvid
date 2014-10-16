package nutella.pendvid;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;
import com.xuggle.xuggler.Utils;

public class Readvid {
	@SuppressWarnings("deprecation")
	public static void readvid(String filename, String outdir) {
		IContainer container = IContainer.make();

		if(container.open(filename, IContainer.Type.READ, null) < 0) {
			throw new IllegalArgumentException
				("could not open file: " + filename);
		}

		int numStreams = container.getNumStreams();
		System.out.println(numStreams);

		int videoStreamId = -1;
		IStreamCoder videoCoder = null;
		for(int i = 0; i < numStreams; i++) {
			IStream stream = container.getStream(i);
	
			IStreamCoder coder = stream.getStreamCoder();
	
			if(coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
				videoStreamId = i;
				videoCoder = coder;
				break;
			}
		}

		if(videoStreamId == -1) {
			throw new RuntimeException("could not find video stream in container" + filename);
		}

		if(videoCoder.open() < 0) {
			throw new RuntimeException("could not open video decoder");
		}

		IVideoResampler resampler = null;
		if(videoCoder.getPixelType() != IPixelFormat.Type.BGR24) {
			resampler = IVideoResampler.make(videoCoder.getWidth(), videoCoder.getHeight(), IPixelFormat.Type.BGR24, videoCoder.getWidth(), videoCoder.getHeight(), videoCoder.getPixelType());
			if(resampler == null) {
				throw new RuntimeException("could not create colour space resampler");
			}
		}

		int frameNo = 0;
		IPacket packet = IPacket.make();
		while(container.readNextPacket(packet) >= 0) {
			if(packet.getStreamIndex() == videoStreamId) {
				IVideoPicture picture = IVideoPicture.make(videoCoder.getPixelType(), videoCoder.getWidth(), videoCoder.getHeight());
				int offset = 0;
				while(offset < packet.getSize()) {
					int bytesDecoded = videoCoder.decodeVideo(picture, packet, offset);
					if(bytesDecoded < 0)
						throw new RuntimeException("got error decoding video");
					offset += bytesDecoded;
			
					if(picture.isComplete()) {
						IVideoPicture newPic = picture;
						if(resampler != null) {
							newPic = IVideoPicture.make(
									resampler.getOutputPixelFormat(), picture.getWidth(),
									picture.getHeight());
							if(resampler.resample(newPic, picture) < 0)
								throw new RuntimeException("could not resample frame");
						}
						if(newPic.getPixelType() != IPixelFormat.Type.BGR24)
							throw new RuntimeException("could not decode video as BGR 24 bit");
						BufferedImage image = Utils.videoPictureToImage(newPic);
				
						try {
							ImageIO.write(image, "jpg", new File(outdir + File.separator + "f" + frameNo + ".jpg"));
						} catch (IOException e) {
							e.printStackTrace();
						}
						System.out.println(outdir + File.separator + "f" + frameNo + ".jpg");
						frameNo++;
					}
				}
			}
		}

		if(videoCoder != null) {
			videoCoder.close();
		}
		if(container != null) {
			container.close();
		}
	}

	public static void main(String[] args) {
		if(args.length < 2) {
			throw new IllegalArgumentException
				("must pass filename as first argument and out dir as second");
		}

		Readvid.readvid(args[0], args[1]);
	}
}
