# acrodb
Embeddable thread-safe flat-file document database for Java

## example
```
Bucket bucket = new Bucket(); // you can pass a Context object to specify the root bucket location (./acrodb/ by default), file system (FileSystems.getDefault() by default), and data producer (gson by default).
Document document = bucket.openDocument("some-document"); // you can also open another bucket instead of a document

SomeBean someBean = new SomeBean();
someBean.message = "hello world";
document.write(someBean);

// better practice in many cases would be to allow the edit method to create the bean instance automatically
document.edit(SomeBean.class, someBean -> {
    someBean.message = "hello world"; // this will automaticaly be written
});

document.save(); // or bucket.save() to recursively save all child documents

class SomeBean {
    String message;
}
```
