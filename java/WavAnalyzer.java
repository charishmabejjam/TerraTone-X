import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;

/** Basic, transparent WAV measurements. Not species recognition or ecological diagnosis. */
public class WavAnalyzer {
  static int u16(byte[] b,int p){return (b[p]&255)|((b[p+1]&255)<<8);} static long u32(byte[] b,int p){return (b[p]&255L)|((b[p+1]&255L)<<8)|((b[p+2]&255L)<<16)|((b[p+3]&255L)<<24);}
  public static void main(String[] a)throws Exception{
    if(a.length!=1)throw new IllegalArgumentException("Usage: WavAnalyzer file.wav"); byte[] b=Files.readAllBytes(Path.of(a[0]));
    if(b.length<44||!new String(b,0,4).equals("RIFF")||!new String(b,8,4).equals("WAVE"))throw new IllegalArgumentException("Only PCM WAV files are supported.");
    int pos=12,fmt=0,ch=0,bits=0,rate=0,data=-1,size=0;
    while(pos+8<=b.length){String id=new String(b,pos,4);long n=u32(b,pos+4);int len=(int)Math.min(n,b.length-pos-8);if(id.equals("fmt ")&&len>=16){fmt=u16(b,pos+8);ch=u16(b,pos+10);rate=(int)u32(b,pos+12);bits=u16(b,pos+22);}if(id.equals("data")){data=pos+8;size=len;break;}pos+=8+len+(len&1);}
    if(fmt!=1||data<0||(bits!=8&&bits!=16&&bits!=24&&bits!=32))throw new IllegalArgumentException("Supported format: integer PCM WAV (8/16/24/32-bit).");
    int bytes=bits/8,frames=size/(bytes*Math.max(1,ch));double sum=0,peak=0,zc=0,prev=0;long count=0;
    for(int i=0;i<frames;i++){double mono=0;for(int c=0;c<ch;c++){int p=data+(i*ch+c)*bytes;long v=0;for(int k=0;k<bytes;k++)v|=(long)(b[p+k]&255)<<(8*k);if(bits==8)v-=128;else if(bits==24&&((v&0x800000)!=0))v|=0xffffffffff000000L;double x=bits==8?v/128.0:bits==16?(short)v/32768.0:bits==24?v/8388608.0:(int)v/2147483648.0;mono+=x;peak=Math.max(peak,Math.abs(x));}mono/=ch;sum+=mono*mono;if(i>0&&((mono>=0)!=(prev>=0)))zc++;prev=mono;count++;}
    double rms=count==0?0:Math.sqrt(sum/count),duration=rate==0?0:(double)frames/rate,zcr=count<2?0:zc/(count-1);
    System.out.printf(Locale.ROOT,"{\"format\":\"PCM WAV\",\"sampleRateHz\":%d,\"channels\":%d,\"bitsPerSample\":%d,\"durationSeconds\":%.4f,\"rms\":%.6f,\"peakAmplitude\":%.6f,\"zeroCrossingRate\":%.6f,\"frames\":%d,\"interpretation\":\"Acoustic measurements only; not species identification or proof of ecological change.\"}%n",rate,ch,bits,duration,rms,peak,zcr,frames);
  }
}
