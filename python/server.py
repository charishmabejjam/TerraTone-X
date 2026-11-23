"""TerraTone X local companion API. Standard-library only; original UI is served unchanged."""
from http.server import ThreadingHTTPServer, SimpleHTTPRequestHandler
from pathlib import Path
from urllib.parse import urlparse
import json, subprocess, tempfile, shutil, os
ROOT = Path(__file__).resolve().parents[1]
WEB = ROOT / 'web'
JAVA = ROOT / 'java'
class Handler(SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs): super().__init__(*args, directory=str(WEB), **kwargs)
    def send_json(self, code, data):
        raw=json.dumps(data).encode(); self.send_response(code); self.send_header('Content-Type','application/json'); self.send_header('Content-Length',str(len(raw))); self.send_header('Access-Control-Allow-Origin','*'); self.end_headers(); self.wfile.write(raw)
    def do_GET(self):
        if urlparse(self.path).path == '/api/health': return self.send_json(200, {'status':'ok','service':'TerraTone X Python companion','java_available':shutil.which('java') is not None})
        if self.path == '/': self.path='/index.html'
        return super().do_GET()
    def do_POST(self):
        if urlparse(self.path).path != '/api/analyze-wav': return self.send_json(404, {'error':'Endpoint not found'})
        length=int(self.headers.get('Content-Length','0'))
        if length <= 44 or length > 50*1024*1024: return self.send_json(400, {'error':'Upload a WAV file between 44 bytes and 50 MB.'})
        data=self.rfile.read(length)
        with tempfile.NamedTemporaryFile(suffix='.wav', delete=False) as f: f.write(data); name=f.name
        try:
            javac=shutil.which('javac'); java=shutil.which('java')
            if javac and java:
                subprocess.run([javac,'-d',str(JAVA),str(JAVA/'WavAnalyzer.java')],check=True,capture_output=True,text=True)
                p=subprocess.run([java,'-cp',str(JAVA),'WavAnalyzer',name],check=True,capture_output=True,text=True)
                return self.send_json(200, json.loads(p.stdout))
            return self.send_json(503, {'error':'Java JDK not found. Install JDK 17+ to use Java WAV analysis.'})
        except subprocess.CalledProcessError as e: return self.send_json(422, {'error':(e.stderr or 'Could not analyze WAV file.')[-1200:]})
        finally: os.unlink(name)
        def log_message(self, fmt, *args):
            super().log_message(fmt, *args)


if __name__ == '__main__':
    print('TerraTone X running: http://127.0.0.1:8000')
    print('Original TerraTone X interface is served unchanged.')
    ThreadingHTTPServer(('127.0.0.1', 8000), Handler).serve_forever()