# COMP3011 – Assignment 3

## Project Overview

This project extends the video player developed in Assignment 2 and the Spring Boot server developed in Assignment 1 to support automatic subtitle generation using OpenAI's Whisper API.

The application extracts audio from a video, sends it to the server for transcription and displays timestamped subtitles during playback.

## Features

- Concurrent video and audio playback.
- Automatic subtitle generation using Whisper.
- Subtitle toggle through the GUI and the S keyboard shortcut.
- Command-line subtitle support using `-S` or `--subtitle-frames`.
- Background audio extraction and transcription.
- Progressive subtitle loading using audio chunks.
- Timestamp-based subtitle rendering.
- Automatic retry when subtitle requests fail.
- Tracking of transcription usage on the server.
- Compatibility with existing video effects.

## Requirements

- Java JDK
- Maven
- Internet connection
- Valid OpenAI API key

## Running the Server

Open the `speechToTextServer` project.

Configure the `OPENAI_API_KEY` environment variable with a valid API key before starting the server.

Build the server using:

```bash
mvn package -DskipTests
```

Run the generated JAR:

```bash
java -jar target/Assignment1-0.0.1-SNAPSHOT.jar
```

The server runs on port 8080.

## Running the Video Player

Open the `VideoPlayerSingleThread` project.

To run the player with subtitles enabled:

```bash
mvn compile javafx:run -Djavafx.args="-S path/to/video.mp4"
```

To run the player with subtitles disabled:

```bash
mvn compile javafx:run -Djavafx.args="path/to/video.mp4"
```

To display the available command-line options:

```bash
mvn compile javafx:run -Djavafx.args="--help"
```

## Subtitle Controls

| Control | Function |
|---|---|
| S | Toggle subtitles on or off |
| Subtitle button | Enable or disable subtitles |
| -S | Start playback with subtitles enabled |
| --subtitle-frames | Start playback with subtitles enabled |

## Client-Server Communication

The video player extracts audio into WAV files and sends them to the Spring Boot server using HTTP multipart requests.

The server communicates with the Whisper API, processes the transcription response and returns subtitles in JSON format.

### API Endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/v1/subtitles` | Generate subtitles from audio |
| GET | `/api/v1/global/usage` | Retrieve total transcription usage in seconds |

## Concurrency and Thread Safety

Audio extraction and subtitle requests are performed on a separate background worker to avoid blocking video playback or the JavaFX application thread.

The implementation uses:

- `ExecutorService` for background tasks.
- `CompletableFuture` for asynchronous server operations.
- `volatile` variables for visibility between threads.
- Synchronized methods to protect shared subtitle loading state.
- `Future` cancellation to stop unnecessary background tasks.
- `DoubleAdder` for thread-safe transcription usage tracking.

Generation identifiers are used to prevent outdated subtitle requests from updating the subtitles of a newly opened video.

## Subtitle Processing

Audio is extracted in smaller chunks to allow subtitles to become available progressively. 
Each subtitle's timestamp is adjusted according to its chunk's position in the original video.

Subtitles are displayed using white sans-serif text with a black outline.
 Text wrapping is applied with a maximum of 42 characters per line and two displayed lines.

## Known Limitations

- Subtitle accuracy depends on the quality of the original audio and the Whisper transcription response.
- Background music and overlapping dialogue may reduce transcription accuracy.
- Subtitles may be delayed depending on network conditions and API response times.
- Splitting audio into chunks may reduce transcription context.
- Optional subtitle caching has not been implemented.

## Author

Charvi Bhandari
Adelaide University
COMP3011 – Cloud and Concurrent Programming