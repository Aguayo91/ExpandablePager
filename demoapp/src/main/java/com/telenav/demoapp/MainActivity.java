package com.telenav.demoapp;

import android.animation.ValueAnimator;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.telenav.demoapp.fragment.BookFragmentAdapter;
import com.telenav.demoapp.fragment.BookFragmentStateAdapter;
import com.telenav.expandablepager.ExpandablePager;
import com.telenav.expandablepager.listener.OnSliderStateChangeListener;
import com.telenav.expandableviewpager.R;

public class MainActivity extends AppCompatActivity {

    int duration = 200;
    boolean two = false;
    private List<Book> list2 = Arrays.asList(
            new Book("Crime and Punishment").setAuthor("Fyodor Dostoyevsky").setUrl("http://ecx.images-amazon.com/images/I/51M%2BDuxvjmL._SX311_BO1,204,203,200_.jpg").setDescription("Crime and Punishment (Russian: Преступлéние и наказáние, tr. Prestupleniye i nakazaniye; IPA: [prʲɪstʊˈplʲenʲɪɪ ɪ nəkɐˈzanʲɪɪ]) is a novel by the Russian author Fyodor Dostoyevsky. It was first published in the literary journal The Russian Messenger in twelve monthly installments during 1866. It was later published in a single volume. It is the second of Dostoyevsky's full-length novels following his return from 10 years of exile in Siberia. Crime and Punishment is considered the first great novel of his \"mature\" period of writing."),
            new Book("The Brothers Karamazov").setAuthor("Fyodor Dostoyevsky").setUrl("http://ecx.images-amazon.com/images/I/51FIyYKsCXL._SX333_BO1,204,203,200_.jpg").setDescription("The Brothers Karamazov (Russian: Бра́тья Карама́зовы, Brat'ya Karamazovy, pronounced [ˈbratʲjə kərɐˈmazəvɨ]), also translated as The Karamazov Brothers, is the final novel by the Russian author Fyodor Dostoyevsky. Dostoyevsky spent nearly two years writing The Brothers Karamazov, which was published as a serial in The Russian Messenger and completed in November 1880. The author died less than four months after its publication."),
            new Book("Demons").setAuthor("Fyodor Dostoyevsky").setUrl("http://ecx.images-amazon.com/images/I/41Q-p2N1neL._SX326_BO1,204,203,200_.jpg").setDescription("Demons (Russian: Бесы, Bésy) is an anti-nihilistic novel by Fyodor Dostoyevsky, first published in the journal The Russian Messenger in 1871-2. It is the third of the four great novels written by Dostoyevsky after his return from Siberian exile, the others being Crime and Punishment (1866), The Idiot (1869) and The Brothers Karamazov (1880). Demons is a social and political satire, a psychological drama, and large scale tragedy. Joyce Carol Oates has described it as \"Dostoevsky's most confused and violent novel, and his most satisfyingly 'tragic' work.\"")
    );
    private List<Book> list = Arrays.asList(
            new Book("Crime and Punishment").setAuthor("Fyodor Dostoyevsky").setUrl("http://ecx.images-amazon.com/images/I/51M%2BDuxvjmL._SX311_BO1,204,203,200_.jpg").setDescription("Crime and Punishment (Russian: Преступлéние и наказáние, tr. Prestupleniye i nakazaniye; IPA: [prʲɪstʊˈplʲenʲɪɪ ɪ nəkɐˈzanʲɪɪ]) is a novel by the Russian author Fyodor Dostoyevsky. It was first published in the literary journal The Russian Messenger in twelve monthly installments during 1866. It was later published in a single volume. It is the second of Dostoyevsky's full-length novels following his return from 10 years of exile in Siberia. Crime and Punishment is considered the first great novel of his \"mature\" period of writing."),
            new Book("The Brothers Karamazov").setAuthor("Fyodor Dostoyevsky").setUrl("http://ecx.images-amazon.com/images/I/51FIyYKsCXL._SX333_BO1,204,203,200_.jpg").setDescription("The Brothers Karamazov (Russian: Бра́тья Карама́зовы, Brat'ya Karamazovy, pronounced [ˈbratʲjə kərɐˈmazəvɨ]), also translated as The Karamazov Brothers, is the final novel by the Russian author Fyodor Dostoyevsky. Dostoyevsky spent nearly two years writing The Brothers Karamazov, which was published as a serial in The Russian Messenger and completed in November 1880. The author died less than four months after its publication."),
            new Book("Demons").setAuthor("Fyodor Dostoyevsky").setUrl("http://ecx.images-amazon.com/images/I/41Q-p2N1neL._SX326_BO1,204,203,200_.jpg").setDescription("Demons (Russian: Бесы, Bésy) is an anti-nihilistic novel by Fyodor Dostoyevsky, first published in the journal The Russian Messenger in 1871-2. It is the third of the four great novels written by Dostoyevsky after his return from Siberian exile, the others being Crime and Punishment (1866), The Idiot (1869) and The Brothers Karamazov (1880). Demons is a social and political satire, a psychological drama, and large scale tragedy. Joyce Carol Oates has described it as \"Dostoevsky's most confused and violent novel, and his most satisfyingly 'tragic' work.\""),
            new Book("Catch-22").setAuthor("Joseph Heller").setUrl("http://ecx.images-amazon.com/images/I/51kqbC3YKvL._SX322_BO1,204,203,200_.jpg").setDescription("Catch-22 is a satirical novel by the American author Joseph Heller. He began writing it in 1953; the novel was first published in 1961. It is frequently cited as one of the greatest literary works of the twentieth century. It uses a distinctive non-chronological third-person omniscient narration, describing events from the points of view of different characters. The separate storylines are out of sequence so that the timeline develops along with the plot."),
            new Book("Animal Farm").setAuthor("George Orwell").setUrl("http://ecx.images-amazon.com/images/I/51EjU6rQjsL._SX318_BO1,204,203,200_.jpg").setDescription("Animal Farm is an allegorical and dystopian novella by George Orwell, first published in England on 17 August 1945. According to Orwell, the book reflects events leading up to the Russian Revolution of 1917 and then on into the Stalinist era of the Soviet Union. Orwell, a democratic socialist, was a critic of Joseph Stalin and hostile to Moscow-directed Stalinism, an attitude that was critically shaped by his experiences during the Spanish Civil War."),
            new Book("To Kill a Mockingbird").setAuthor("Harper Lee").setUrl("http://ecx.images-amazon.com/images/I/51grMGCKivL._SX307_BO1,204,203,200_.jpg").setDescription("To Kill a Mockingbird is a novel by Harper Lee published in 1960. It was immediately successful, winning the Pulitzer Prize, and has become a classic of modern American literature. The plot and characters are loosely based on the author's observations of her family and neighbors, as well as on an event that occurred near her hometown in 1936, when she was 10 years old."),
            new Book("Fahrenheit 451").setAuthor("Ray Bradbury").setUrl("http://ecx.images-amazon.com/images/I/41Cx8mY2UNL._SX324_BO1,204,203,200_.jpg").setDescription("Fahrenheit 451 is a dystopian novel by Ray Bradbury published in 1953. It is regarded as one of his best works. The novel presents a future American society where books are outlawed and \"firemen\" burn any that are found. The title refers to the temperature that Bradbury asserted to be the autoignition temperature of paper. (In reality, scientists place the autoignition temperature of paper anywhere from high 440 degrees Fahrenheit to some 30 degrees hotter, depending on the study and type of paper.)")
    );
    private ExpandablePager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setDownsampleEnabled(true)
                .build();
        Fresco.initialize(this, config);
        setTitle("Books");
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        final List<Book> myList = new ArrayList<>(list);

        //use PageAdapter
        final BookAdapter adapter = new BookAdapter(myList);
        //or FragmentAdapter
        //final BookFragmentAdapter adapter = new BookFragmentAdapter(getSupportFragmentManager(), myList);
        //or FragmentStateAdapter
        //final BookFragmentStateAdapter adapter = new BookFragmentStateAdapter(getSupportFragmentManager(), myList);

        pager = (ExpandablePager) findViewById(R.id.container);
        //pager.setAnimationDuration(duration);
        //pager.setCollapsedHeight((int) getResources().getDimension(R.dimen.header_height));
        //pager.setMode(ExpandablePager.MODE_REGULAR);
        pager.setAdapter(adapter);
        pager.setOnSliderStateChangeListener(new OnSliderStateChangeListener() {

            @Override
            public void onStateChanged(View page, int index, int state) {
                toggleContent(page, state, duration);
            }

            @Override
            public void onPageChanged(View page, int index, int state) {
                toggleContent(page, state, 0);
            }
        });

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.book_list);
        mRecyclerView.setHasFixedSize(true);

        int columns = isTablet() ? 2 : 1;
        columns += isLandscape() ? 1 : 0;
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, columns);
        mRecyclerView.setLayoutManager(mLayoutManager);

        BookGridAdapter a = new BookGridAdapter(list);

        a.setListener(new OnItemClickedListener() {
            @Override
            public void onItemClicked(int index) {
                pager.setCurrentItem(index, false);
                pager.animateToState(ExpandablePager.STATE_EXPANDED);
            }
        });

        mRecyclerView.setAdapter(a);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.shbutton:
                pager.animateToState(pager.getSliderState() == ExpandablePager.STATE_HIDDEN ? ExpandablePager.STATE_COLLAPSED : ExpandablePager.STATE_HIDDEN);
                return true;

            case R.id.ccbutton:
                pager.setAdapter(new BookAdapter((two = !two) ? list2 : list));
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void toggleContent(final View page, final int state, int duration) {
        final int headerHeight = (int) getResources().getDimension(R.dimen.header_height);
        if (page != null && isTablet()) {
            ValueAnimator animator = state == ExpandablePager.STATE_EXPANDED ? ValueAnimator.ofFloat(1, 0) : ValueAnimator.ofFloat(0, 1);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    page.findViewById(R.id.header_title).setTranslationY(25 * (1 - ((Float) animation.getAnimatedValue())));
                    page.findViewById(R.id.header_title).setTranslationX(-headerHeight * (1 - ((Float) animation.getAnimatedValue())));
                    page.findViewById(R.id.header_subtitle).setAlpha(((Float) animation.getAnimatedValue()));
                    page.findViewById(R.id.header_img).setAlpha(((Float) animation.getAnimatedValue()));
                    page.findViewById(R.id.header_action).setAlpha(((Float) animation.getAnimatedValue()));
                }
            });
            animator.setDuration((long) (duration * .5));
            animator.setInterpolator(new FastOutSlowInInterpolator());
            animator.start();
        }
        if (getSupportActionBar() != null)
            getSupportActionBar().setElevation(state == ExpandablePager.STATE_EXPANDED ? 0 : getResources().getDisplayMetrics().density * 8);
    }

    private boolean isTablet() {
        int sizeMask = (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK);
        return sizeMask == Configuration.SCREENLAYOUT_SIZE_LARGE
                || sizeMask == Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    private boolean isLandscape() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }
}
