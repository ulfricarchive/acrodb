# acrodb
Embeddable flat-file document database for Java

## example
```
Bucket bucket = new Bucket(); // you can pass a Path object to specify where the bucket will be. By default, it's at ./acrodb/
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
