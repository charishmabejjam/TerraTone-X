# TerraTone X — original prototype + Python/Java companion

This package preserves the supplied original TerraTone X page byte-for-byte at `web/index.html`. The forest-themed login, navigation, dashboard, Acoustic Lab, preview/recording, reports, SoundDNA, EcoChange, TerraMap, history and assistant remain in the original browser interface.

## Run on Windows
1. Install Python 3.10 or newer.
2. Double-click `RUN_WINDOWS.bat`, or run `python python/server.py` from this folder.
3. Open http://127.0.0.1:8000

No Maven, Node, Flask or pip packages are required.

## Python and Java roles
- Python: local HTTP server, API routing, upload validation and service orchestration.
- Java: WAV metadata and signal measurements (sample rate, channels, duration, RMS, peak amplitude and zero-crossing rate).
- HTML/CSS/JavaScript: retained original browser interface and its existing browser-side workflow.

The Java WAV endpoint is `POST /api/analyze-wav`; health check is `GET /api/health`. Java analysis requires JDK 17+ (`java` and `javac` on PATH).

**Scientific limitation:** Signal measurements are not species identification, biodiversity estimates or proof of ecosystem change. Demo records are illustrative.