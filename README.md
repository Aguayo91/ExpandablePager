# ExpandablePager
Layout that contains a ViewPager and can slide vertically between 2 states (expanded and collapsed).

![alt tag](https://github.com/Telenav/ExpandablePager/blob/master/gif/expandablepager.gif?token=AMYIVqqzN85dOdOY4DOfk2wC-i8R7RxNks5W1HArwA%3D%3D "Demo")
#Setup
###Step 1
Add the following line to the ```dependencies``` section of your ```build.gradle``` file
```gradle
//todo
```
###Step 2
Extend ExpandablePagerAdapter class and override ```instantiateItem```
```java
public class MyAdapter extends ExpandablePagerAdapter<Book> {
    
    ...

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final ViewGroup rootView = (ViewGroup) LayoutInflater.from(container.getContext()).inflate(R.layout.page, container, false); //inflate view

        ...

        return attach(container, rootView, position); //don't forget to return the result of attach()
    }
}
```
###Step 3
Add ExpandablePager to a layout
```xml
<?xml version="1.0" encoding="utf-8"?>
RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <com.telenav.expandablepager.ExpandablePager
        android:id="@+id/container"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_gravity="bottom"
        android:layout_alignParentBottom="true" /> //pin the pager to the bootom of the screen using layout_gravity or layout_alignParentBottom
</RelativeLayout>
```
###Step 4
Create and pass your adapter to the ExpandablePager
```java 
    MyAdapter adapter = new MyAdapter(myData);
    ExpandablePager pager = (ExpandablePager) findViewById(R.id.container);
    pager.setAdapter(adapter);
```
###Step 5 (optional)
* Set collapsed height & animation duration via xml
```xml 
    app:animation_duration="200"
    app:collapsed_height="@dimen/header_height"
```
or programmatically
```java 
    pager.setAnimationDuration(duration);
    pager.setCollapsedHeight((int) getResources().getDimension(R.dimen.header_height));
```
* Set display mode (MODE_REGULAR or MODE_FIXED)
```java 
    pager.setMode(ExpandablePager.MODE_REGULAR);
```
#Extras
Change pager state (supported states are STATE_COLLAPSED, STATE_EXPANDED, and STATE_HIDDEN) 
```java  
    pager.setState(ExpandablePager.STATE_COLLAPSED)
```
Animate pager state change
```java  
    pager.animateToState(ExpandablePager.STATE_COLLAPSED)
```
Set current item
```java  
    pager.setCurrentItem(index, false);
```
