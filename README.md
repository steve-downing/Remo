# Remo

Remo is an easy RPC framework for Java that stays out of your way. No config files and minimal boilerplate are necessary to get a simple service up and running.

## Basic Usage

First, create an interface for the service you'd like to provide. This interface is the contract describing what the service expects from the client and what it'll give in return. Any method signatures work as long as all the parameters and return values are Serializable.

# Example

    public interface BookmarkService {
        public BookmarkId createBookmark(String url, String title, String description);
        public boolean deleteBookmark(BookmarkId id);
    }

Server-side:

    // This instantiates a BookmarkServiceImpl and uses it to handle requests.
    // It honors the BookMarkService contract described above and serves it on port 8080.
    new NetServiceRunner().runService(new BookmarkServiceImpl(), BookmarkService.class, 8080);

You provide the service implementation, but it's just one line of code and zero lines of configuration to fire up your service.

Client-side:

    // This creates a hook to a service we've hosted on api.bookmarks.com:8080.
    // A call to any bookmarkService method will execute remotely.
    BookmarkService bookmarkService =
        new RemoteServiceClientFactory("api.bookmarks.com", 8080).getService(BookmarkService.class);

The above code is all it takes to get a hook to the remote service.

## Futures

Under the hood, simple service methods you invoke will serialize your arguments, send the request out, and block the thread's execution while awaiting a response. An alternative is to have your method return a Future, making execution asynchronous. The Future returned by the method need not be Serializable.

    public interface BookmarkService {
        public Future<BookmarkId> createBookmark(String url, String title, String description);
    }

If the client invokes the above createBookmark method, thread execution will continue as soon as the arguments are serialized, not waiting for the request to complete. Built-in Java Futures, Guava ListenableFutures, and Remo Futures are all supported here.
