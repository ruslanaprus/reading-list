# Book Reading List

This application uses configurable AI chat models and book information providers.
Configuration is done via application properties, but you can override settings at runtime 
using command-line arguments or environment variables, so there's no need to edit `application.properties` directly.

## Configuration

The application supports the following configurable properties:

### Chat Models (`chat.model`)
Choose which AI chat model to use:

- `gemini`: Uses Google's Gemini model via Vertex AI.
- `ollama`: Uses a local instance of Ollama.

### Book Data Providers (books.provider)
Choose which book API provider to use:

- `google`: Uses the Google Books API.
- `openLibrary`: Uses the Open Library API.
- `wikiBooks`: Uses the WikiBooks API.