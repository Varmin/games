## TODO
* API动态更换url
* ApiService返回值：Call可以手动控制异步、Observable在哪控制的？
```
Api.service.testGet2()

    // TODO: by HY, 2020/7/15 封装线程切换   
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

    // TODO: by HY, 2020/7/15 传入封装类，返回Observer 
    .subscribe(object : BaseObserver<List<TestBean>>() {
    override fun onSuccess(data: List<TestBean>) {
        println("${javaClass.simpleName}.onSuccess: ${data?.size}, ${Thread.currentThread().name}")
    }
})
```
* ButterKnife能引入，不能解析BindView；
    * Kotlin中虽然使用extensions有些不太方便，但毕竟已经有了，是否可以有其它更好的方法？
    * 即使extensions可以替代，但是不能bindclick，一样很麻烦