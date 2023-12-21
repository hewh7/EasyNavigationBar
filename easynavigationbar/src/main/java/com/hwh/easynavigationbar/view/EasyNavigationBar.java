package com.hwh.easynavigationbar.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.ScreenUtils;
import com.hwh.easynavigationbar.R;
import com.hwh.easynavigationbar.adapter.ViewPagerAdapter;
import com.hwh.easynavigationbar.utils.NavigationUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hwh
 * @desc 底部导航栏
 */
public class EasyNavigationBar extends LinearLayout {

    //文字集合
    private String[] titleItems = new String[]{};
    //未选择 图片集合
    private int[] normalIconItems = new int[]{};
    //已选择 图片集合
    private int[] selectIconItems = new int[]{};
    //fragment集合
    private List<Fragment> fragmentList = new ArrayList<>();
    //fragment管理器
    private FragmentManager fragmentManager;
    //页面加载适配器
    private ViewPagerAdapter adapter;

    //容器视图
    private RelativeLayout contentView;
    private RelativeLayout addContainerLayout;
    private LinearLayout navigationLayout;
    //分割线
    private View lineView;
    private View empty_line;
    private ViewGroup addViewLayout;

    //Tab数量
    private int tabCount = 0;
    //红点集合
    private final List<View> hintPointList = new ArrayList<>();
    //消息数量集合
    private final List<TextView> msgPointList = new ArrayList<>();
    //底部Image集合
    private final List<ImageView> imageViewList = new ArrayList<>();
    //底部Text集合
    private final List<TextView> textViewList = new ArrayList<>();
    //底部TabLayout（除中间加号）
    private final List<View> tabList = new ArrayList<>();
    //视图滑动切换工具
    private ViewPager mViewPager;

    //private GestureDetector detector;
    //Tab点击动画效果
//    private Techniques anim = null;
    //ViewPager切换动画
    private boolean smoothScroll = false;

    //导航栏背景
    private int navigationBackground;
    //导航栏高度
    private float navigationHeight;
    //是否可以滚动
    private boolean canScroll;
    //导航栏内容类型
    private int contentType;
    //导航栏模式: 0默认, 1中间加号, 2中间自定义
    private int mode;
    //true  ViewPager在Navigation上面
    //false ViewPager和Navigation重叠
    private boolean hasPadding;
    //只是导航没有ViewPager
    private boolean onlyNavigation;
    //记录位置
    private int currentPosition;
    
    //图标大小
    private int iconSize;
    //图标缩放样式
    private ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER_INSIDE;
    //Tab内容布局方式
    private int tabContentRule;
    //Tab内容距底部距离
    private int tabContentBottomMargin;
    //Tab文字距Tab图标的距离
    private float tabTextTop;
    //Tab文字大小
    private float tabTextSize;
    //字体显示为DP还是SP, 默认1为DP, 2为SP
    private int textSizeType;
    //未选中Tab字体颜色
    private int normalTextColor;
    //选中字体颜色
    private int selectTextColor;
    
    //提示红点大小
    private float hintPointSize;
    //提示红点距Tab图标右侧的距离
    private float hintPointLeft;
    //提示红点距图标顶部的距离
    private float hintPointTop;

    //消息红点字体大小
    private float msgPointTextSize;
    //消息红点大小
    private float msgPointSize;
    //消息红点99+的长度
    private float msgPointMoreWidth;
    //消息红点99+的高度
    private float msgPointMoreHeight;
    //消息红点99+的半径
    private int msgPointMoreRadius;
    //消息红点颜色
    private int msgPointColor;
    //消息红点距Tab图标右侧的距离   默认为Tab图标的一半
    private float msgPointLeft;
    //消息红点距图标顶部的距离  默认为Tab图标的一半
    private float msgPointTop;
    
    //分割线高度
    private float lineHeight;
    //分割线颜色
    private int lineColor;

    //加号图标大小
    private float centerIconSize;
    //加号布局高度
    private float centerLayoutHeight = navigationHeight;
    //加号布局距底边距
    private float centerLayoutBottomMargin;

    //RULE_CENTER 居中只需调节centerLayoutHeight 默认和navigationHeight相等 此时centerLayoutBottomMargin属性无效
    //RULE_BOTTOM centerLayoutHeight属性无效、自适应、只需调节centerLayoutBottomMargin距底部的距离
    private int centerLayoutRule = RULE_CENTER;
    public static final int RULE_CENTER = 0;
    public static final int RULE_BOTTOM = 1;
    
    //true 点击加号切换fragment
    //false 点击加号不切换fragment进行其他操作（跳转界面等）
    private boolean centerAsFragment;
    //自定义加号view
    private View customAddView;
    private float centerTextSize;
    //加号文字未选中颜色（默认同其他tab）
    private int centerNormalTextColor;
    //加号文字选中颜色（默认同其他tab）
    private int centerSelectTextColor;
    //加号文字距离顶部加号的距离
    private float centerTextTopMargin;
    //是否和其他tab文字底部对齐
    private boolean centerAlignBottom;
    private ImageView centerImage;

    //中间布局的图片资源
    private int centerImageRes;
    //中间布局的文字
    private String centerTextStr;

    private OnTabClickListener onTabClickListener;
    private OnCenterTabSelectListener onCenterTabClickListener;
    private OnTabLoadListener onTabLoadListener;

    public EasyNavigationBar(Context context) {
        super(context);
        initViews(context, null);
    }

    public EasyNavigationBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews(context, attrs);
    }

    private void initViews(Context context, AttributeSet attrs) {

        defaultSetting();

        contentView = (RelativeLayout) View.inflate(context, R.layout.container_layout, null);
        addContainerLayout = contentView.findViewById(R.id.add_rl);
        navigationLayout = contentView.findViewById(R.id.navigation_ll);
        lineView = contentView.findViewById(R.id.common_horizontal_line);
        empty_line = contentView.findViewById(R.id.empty_line);
        addViewLayout = contentView.findViewById(R.id.add_view_ll);

        //设置标记
        navigationLayout.setTag(-100);
        lineView.setTag(-100);
        empty_line.setTag(-100);

        //检索此上下文主题中息的样式属性信
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.EasyNavigationBar);
        parseStyle(attributes);

        addView(contentView);
    }

    /**
     * 重置各个参数
     */
    public void defaultSetting() {
        this.titleItems = new String[]{};
        this.normalIconItems = new int[]{};
        this.selectIconItems = new int[]{};
        this.fragmentList = new ArrayList<>();
        if (this.adapter != null) {
            this.adapter.notifyDataSetChanged();
        }

        //图标大小
        iconSize = NavigationUtil.sp2px(getContext(), 22);
        //图标缩放类型
        scaleType = ImageView.ScaleType.CENTER_INSIDE;

        //Tab文字距Tab图标的距离
        tabTextTop = NavigationUtil.dip2px(getContext(), 2);
        //Tab文字大小
        tabTextSize = 12;
        //未选中Tab字体颜色
        normalTextColor = Color.parseColor("#666666");
        //选中字体颜色
        selectTextColor = Color.parseColor("#333333");
        //字体单位类型: 1是dp, 2是sp
        textSizeType = 1;

        //分割线高度
        lineHeight = 1;
        //分割线颜色
        lineColor = Color.parseColor("#f7f7f7");
        //导航栏背景颜色
        navigationBackground = Color.parseColor("#ffffff");
        //导航栏高度
        navigationHeight = NavigationUtil.dip2px(getContext(), 60);
        //导航栏模式类型: 默认, 中间带按钮(加号), 中间带按钮(加号)且自定义view
        mode = NavigationMode.MODE_NORMAL;
        //是否可以滚动
        canScroll = false;
        //true  ViewPager在Navigation上面
        //false ViewPager和Navigation重叠
        hasPadding = true;
        //导航栏内容类型: 默认, 仅图片, 仅文字
        contentType = TabContentType.TYPE_NORMAL;
        //标签点击事件监听
        onTabClickListener = null;
        //标签内容规则
        tabContentRule = 0;
        //标签距离底部边距
        tabContentBottomMargin = 0;

//        //Tab点击动画效果
//        anim = null;
        //ViewPager切换动画
        smoothScroll = false;

        //提示红点大小
        hintPointSize = NavigationUtil.sp2px(getContext(), 6);
        //提示红点距Tab图标右侧的距离
        hintPointLeft = NavigationUtil.dip2px(getContext(), -3);
        //提示红点距图标顶部的距离
        hintPointTop = NavigationUtil.dip2px(getContext(), -3);

        //消息红点字体大小
        msgPointTextSize = 11;
        //消息红点大小
        msgPointSize = NavigationUtil.dip2px(getContext(), 16);
        //消息红点距Tab图标右侧的距离 默认为Tab图标的一半
        msgPointLeft = NavigationUtil.dip2px(getContext(), -10);
        //消息红点距图标顶部的距离 默认为Tab图标的一半
        msgPointTop = NavigationUtil.dip2px(getContext(), -12);
        //消息红点99+的长度
        msgPointMoreWidth = NavigationUtil.dip2px(getContext(), 30);
        //消息红点99+的高度
        msgPointMoreHeight = NavigationUtil.dip2px(getContext(), 16);
        //消息红点99+的半径
        msgPointMoreRadius = 10;
        //消息红点颜色
        msgPointColor = Color.parseColor("#ff0000");

        //中心图标大小, 高度, 距底边距
        centerIconSize = 0;
        centerLayoutHeight = navigationHeight;
        centerLayoutBottomMargin = NavigationUtil.dip2px(getContext(), 10);
        //RULE_CENTER 居中只需调节centerLayoutHeight 默认和navigationHeight相等 此时centerLayoutBottomMargin属性无效
        //RULE_BOTTOM centerLayoutHeight属性无效、自适应、只需调节centerLayoutBottomMargin距底部的距离
        centerLayoutRule = RULE_CENTER;
        //true 点击加号切换fragment
        //false 点击加号不切换fragment进行其他操作（跳转界面等）
        centerAsFragment = false;
        //加号文字大小
        centerTextSize = 0;
        //加号文字未选中颜色（默认同其他tab）
        centerNormalTextColor = 0;
        //加号文字选中颜色（默认同其他tab）
        centerSelectTextColor = 0;
        //加号文字距离顶部加号的距离
        centerTextTopMargin = NavigationUtil.dip2px(getContext(), 3);
        //是否和其他tab文字底部对齐
        centerAlignBottom = false;
        //加号文字内容
        centerTextStr = "";
        //加号点击事件监听
        onCenterTabClickListener = null;
    }

    private void parseStyle(TypedArray attributes) {
        if (attributes != null) {
            //要放在前面
            textSizeType = attributes.getInt(R.styleable.EasyNavigationBar_Easy_textSizeType, textSizeType);

            tabTextSize = NavigationUtil.compareTo(getContext(), attributes.getDimension(R.styleable.EasyNavigationBar_Easy_tabTextSize, 0), tabTextSize, textSizeType);
            tabTextTop = attributes.getDimension(R.styleable.EasyNavigationBar_Easy_tabTextTop, tabTextTop);
            iconSize = (int) attributes.getDimension(R.styleable.EasyNavigationBar_Easy_tabIconSize, iconSize);
            normalTextColor = attributes.getColor(R.styleable.EasyNavigationBar_Easy_tabNormalColor, normalTextColor);
            selectTextColor = attributes.getColor(R.styleable.EasyNavigationBar_Easy_tabSelectColor, selectTextColor);
            
            navigationHeight = attributes.getDimension(R.styleable.EasyNavigationBar_Easy_navigationHeight, navigationHeight);
            navigationBackground = attributes.getColor(R.styleable.EasyNavigationBar_Easy_navigationBackground, navigationBackground);
            hasPadding = attributes.getBoolean(R.styleable.EasyNavigationBar_Easy_hasPadding, hasPadding);

            msgPointColor = attributes.getColor(R.styleable.EasyNavigationBar_Easy_msgPointColor, msgPointColor);
            msgPointMoreWidth = attributes.getDimension(R.styleable.EasyNavigationBar_Easy_msgPointMoreWidth, msgPointMoreWidth);
            msgPointMoreHeight = attributes.getDimension(R.styleable.EasyNavigationBar_Easy_msgPointMoreHeight, msgPointMoreHeight);
            msgPointMoreRadius = attributes.getInt(R.styleable.EasyNavigationBar_Easy_msgPointMoreRadius, msgPointMoreRadius);

            hintPointSize = attributes.getDimension(R.styleable.EasyNavigationBar_Easy_hintPointSize, hintPointSize);
            msgPointSize = attributes.getDimension(R.styleable.EasyNavigationBar_Easy_msgPointSize, msgPointSize);
            hintPointLeft = attributes.getDimension(R.styleable.EasyNavigationBar_Easy_hintPointLeft, hintPointLeft);
            msgPointTop = attributes.getDimension(R.styleable.EasyNavigationBar_Easy_msgPointTop, -iconSize * 3 / 5);
            hintPointTop = attributes.getDimension(R.styleable.EasyNavigationBar_Easy_hintPointTop, hintPointTop);

            msgPointLeft = attributes.getDimension(R.styleable.EasyNavigationBar_Easy_msgPointLeft, -iconSize / 2);
            msgPointTextSize = NavigationUtil.compareTo(getContext(), attributes.getDimension(R.styleable.EasyNavigationBar_Easy_msgPointTextSize, 0), msgPointTextSize, textSizeType);

            lineHeight = attributes.getDimension(R.styleable.EasyNavigationBar_Easy_lineHeight, lineHeight);
            lineColor = attributes.getColor(R.styleable.EasyNavigationBar_Easy_lineColor, lineColor);
            
            //加号属性
            centerIconSize = attributes.getDimension(R.styleable.EasyNavigationBar_Easy_centerIconSize, centerIconSize);
            centerSelectTextColor = attributes.getColor(R.styleable.EasyNavigationBar_Easy_centerSelectTextColor, centerSelectTextColor);
            centerNormalTextColor = attributes.getColor(R.styleable.EasyNavigationBar_Easy_centerNormalTextColor, centerNormalTextColor);
            centerTextSize = NavigationUtil.compareTo(getContext(), attributes.getDimension(R.styleable.EasyNavigationBar_Easy_centerTextSize, 0), centerTextSize, textSizeType);
            centerTextTopMargin = attributes.getDimension(R.styleable.EasyNavigationBar_Easy_centerTextTopMargin, centerTextTopMargin);
            centerLayoutBottomMargin = attributes.getDimension(R.styleable.EasyNavigationBar_Easy_centerLayoutBottomMargin, centerLayoutBottomMargin);
            centerAlignBottom = attributes.getBoolean(R.styleable.EasyNavigationBar_Easy_centerAlignBottom, centerAlignBottom);
            centerLayoutHeight = attributes.getDimension(R.styleable.EasyNavigationBar_Easy_centerLayoutHeight, navigationHeight + lineHeight);
            centerLayoutRule = attributes.getInt(R.styleable.EasyNavigationBar_Easy_centerLayoutRule, centerLayoutRule);
            centerAsFragment = attributes.getBoolean(R.styleable.EasyNavigationBar_Easy_centerAsFragment, centerAsFragment);

            int type = attributes.getInt(R.styleable.EasyNavigationBar_Easy_scaleType, 0);
            if (type == 0) {
                scaleType = ImageView.ScaleType.CENTER_INSIDE;
            } else if (type == 1) {
                scaleType = ImageView.ScaleType.CENTER_CROP;
            } else if (type == 2) {
                scaleType = ImageView.ScaleType.CENTER;
            } else if (type == 3) {
                scaleType = ImageView.ScaleType.FIT_CENTER;
            } else if (type == 4) {
                scaleType = ImageView.ScaleType.FIT_END;
            } else if (type == 5) {
                scaleType = ImageView.ScaleType.FIT_START;
            } else if (type == 6) {
                scaleType = ImageView.ScaleType.FIT_XY;
            } else if (type == 7) {
                scaleType = ImageView.ScaleType.MATRIX;
            }
            
            attributes.recycle();
        }
    }

    /**
     * 设置ViewType
     */
    public EasyNavigationBar setupWithViewPager(@NonNull ViewPager viewPager) {
//        final PagerAdapter adapter = viewPager.getAdapter();
//        if (adapter == null) {
//            throw new IllegalArgumentException("ViewPager does not have a PagerAdapter set");
//        }
        onlyNavigation = true;
        mViewPager = viewPager;
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                selectTab(position, smoothScroll, false);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return this;
    }

    /**
     * 构建导航栏
     */
    public void build() {
        if (centerLayoutHeight < navigationHeight + lineHeight) {
            centerLayoutHeight = navigationHeight + lineHeight;
        }

        if (centerLayoutRule == RULE_CENTER) {
            RelativeLayout.LayoutParams addLayoutParams = (RelativeLayout.LayoutParams) addContainerLayout.getLayoutParams();
            addLayoutParams.height = (int) centerLayoutHeight;
            addContainerLayout.setLayoutParams(addLayoutParams);
        } else if (centerLayoutRule == RULE_BOTTOM) {
           /* RelativeLayout.LayoutParams addLayoutParams = (RelativeLayout.LayoutParams) addContainerLayout.getLayoutParams();
            if ((centerIconSize + addIconBottom) > (navigationHeight + 1))
                addLayoutParams.height = (int) (centerIconSize + addIconBottom);
          else
                addLayoutParams.height = (int) (navigationHeight + 1);
            addContainerLayout.setLayoutParams(addLayoutParams);*/  
        }

        //设置导航栏的背景颜色和高度
        navigationLayout.setBackgroundColor(navigationBackground);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) navigationLayout.getLayoutParams();
        params.height = (int) navigationHeight;
        navigationLayout.setLayoutParams(params);

        //设置分割线的背景颜色和高度
        lineView.setBackgroundColor(lineColor);
        RelativeLayout.LayoutParams lineParams = (RelativeLayout.LayoutParams) lineView.getLayoutParams();
        lineParams.height = (int) lineHeight;
        lineView.setLayoutParams(lineParams);

        //若没有设置中间添加的文字字体大小、颜色、则同其他Tab一样
        if (centerTextSize == 0) {
            centerTextSize = tabTextSize;
        }
        if (centerNormalTextColor == 0) {
            centerNormalTextColor = normalTextColor;
        }
        if (centerSelectTextColor == 0) {
            centerSelectTextColor = selectTextColor;
        }

        //检查是否可以构建
        if (!checkCanBuild()) {
            return;
        }

        switch (mode) {
            case NavigationMode.MODE_NORMAL:
                buildNavigation();
                break;
            case NavigationMode.MODE_ADD:
                buildAddNavigation();
                break;
            case NavigationMode.MODE_ADD_VIEW:
                buildAddViewNavigation();
                break;
            default:
                break;
        }
    }

    /**
     * 验证能否构建
     */
    private boolean checkCanBuild() {
        if (titleItems.length < 1 && normalIconItems.length < 1) {
            Log.e(getClass().getName(), "titleItems和normalIconItems不能同时为空");
            return false;
        }
        buildCommonNavigation();
        return true;
    }

    /**
     * 构建导航栏前的通用操作
     */
    private void buildCommonNavigation() {
        //只有导航栏, 没有fragment切换
        onlyNavigation = fragmentList == null || fragmentList.size() < 1 || fragmentManager == null;

        //设置导航栏内容类型和标签数量
        if (titleItems == null || titleItems.length < 1) {
            contentType = TabContentType.TYPE_ONLY_IMAGE;
            tabCount = normalIconItems.length;
        } else if (normalIconItems == null || normalIconItems.length < 1) {
            contentType = TabContentType.TYPE_ONLY_TEXT;
            tabCount = titleItems.length;
        } else {
            contentType = TabContentType.TYPE_NORMAL;
            tabCount = Math.max(titleItems.length, normalIconItems.length);
        }

        //判断如果是中间加号或者中间自定义的情况则终止后续操作
        if (isAddPage() && tabCount % 2 == 1) {
            Log.e(getClass().getName(), "1.5.0之后、添加中间Tab、则普通Tab数量应为偶数");
            return;
        }

        //如果没有设置选中图标, 那么将选中图标设置为未选中图标, 点击时不改变
        if (selectIconItems == null || selectIconItems.length < 1) {
            selectIconItems = normalIconItems;
        }

        removeNavigationAllView();

        if (!onlyNavigation) {
            setViewPagerAdapter();
        }

        if (hasPadding) {
            if (getViewPager() != null) {
                getViewPager().setPadding(0, 0, 0, (int) (navigationHeight + lineHeight));
            }
        }
    }

    /**
     * 是否有中间局部
     */
    private boolean isAddPage() {
        return mode == NavigationMode.MODE_ADD || mode == NavigationMode.MODE_ADD_VIEW;
    }

    /**
     * 移除导航栏所有视图
     */
    private void removeNavigationAllView() {
        for (int i = 0; i < addContainerLayout.getChildCount(); i++) {
            if (addContainerLayout.getChildAt(i).getTag() == null) {
                addContainerLayout.removeViewAt(i);
            }
        }
        msgPointList.clear();
        hintPointList.clear();
        imageViewList.clear();
        textViewList.clear();
        tabList.clear();

        navigationLayout.removeAllViews();
    }

    /**
     * 添加ViewPager
     */
    private void setViewPagerAdapter() {
        if (mViewPager == null) {
            mViewPager = new CustomViewPager(getContext());
            mViewPager.setId(R.id.vp_layout);
            contentView.addView(mViewPager, 0);
        }
        adapter = new ViewPagerAdapter(fragmentManager, fragmentList);
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(10);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                selectTab(position, smoothScroll, false);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        ((CustomViewPager) getViewPager()).setCanScroll(canScroll);
    }

    /**
     * 构建导航栏
     */
    public void buildNavigation() {
        post(() -> {
            for (int i = 0; i < tabCount; i++) {
                addTabItemView(i);
            }
            selectNormalTabUI(currentPosition, false);
            if (onTabLoadListener != null) {
                onTabLoadListener.onTabLoadCompleteEvent();
            }
        });
    }

    /**
     * 构建中间带按钮的navigation
     */
    public void buildAddNavigation() {
        if (centerImageRes == 0) {
            Log.e("EasyNavigation", "MODE_ADD模式下centerImageRes不能为空");
            return;
        }
        post(() -> {
            for (int i = 0; i < tabCount; i++) {
                if (i == tabCount / 2) {
                    addCenterTabView(i);
                }
                addTabItemView(i);
            }
            selectNormalTabUI(currentPosition, false);
            if (onTabLoadListener != null) {
                onTabLoadListener.onTabLoadCompleteEvent();
            }
        });
    }

    /**
     * 自定义中间按钮
     */
    public void buildAddViewNavigation() {
        post(() -> {
            for (int i = 0; i < tabCount; i++) {
                if (i == tabCount / 2) {
                    addCenterTabCustomView(i);
                }
                addTabItemView(i);
            }
            selectNormalTabUI(currentPosition, false);
            if (onTabLoadListener != null) {
                onTabLoadListener.onTabLoadCompleteEvent();
            }
        });
    }

    /**
     * 生成普通Tab的布局
     */
    private void addTabItemView(final int position) {
        View itemView = View.inflate(getContext(), R.layout.navigation_tab_layout, null);

        LinearLayout ll_tab_content = itemView.findViewById(R.id.ll_tab_content);
        RelativeLayout.LayoutParams llParams = (RelativeLayout.LayoutParams) ll_tab_content.getLayoutParams();

        if (tabContentRule == 0) {
            llParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        } else {
            llParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            llParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            llParams.bottomMargin = tabContentBottomMargin;
        }

        ll_tab_content.setLayoutParams(llParams);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        int index;
        if (isCenterAsFragment()) {
            index = position < (tabCount / 2) ? position : position + 1;
        } else {
            index = position;
        }
        final int finalIndex = index;

        int barWidth = getWidth();
        if (barWidth <= 0) {
            barWidth = ScreenUtils.getScreenWidth();
        }
        if (mode == NavigationMode.MODE_NORMAL) {
            params.width = barWidth / tabCount;
        } else if (mode == NavigationMode.MODE_ADD) {
            params.width = barWidth / (tabCount + 1);
        } else if (mode == NavigationMode.MODE_ADD_VIEW) {
            params.width = barWidth / (tabCount + 1);
        }
        itemView.setTag(R.id.tag_view_position, position);
        itemView.setOnClickListener(view -> {
            if (onTabClickListener != null) {
                if (currentPosition == position) {
                    onTabClickListener.onTabReSelectEvent(view, currentPosition);
                }
                if (!onTabClickListener.onTabSelectEvent(view, position)) {
                    selectTab(finalIndex, smoothScroll);
                }
            } else {
                selectTab(finalIndex, smoothScroll);
            }
        });

        itemView.setLayoutParams(params);

        View hintPoint = itemView.findViewById(R.id.red_point);

        //提示红点
        RelativeLayout.LayoutParams hintPointParams = (RelativeLayout.LayoutParams) hintPoint.getLayoutParams();
        hintPointParams.bottomMargin = (int) hintPointTop;
        hintPointParams.width = (int) hintPointSize;
        hintPointParams.height = (int) hintPointSize;
        hintPointParams.leftMargin = (int) hintPointLeft;
        NavigationUtil.setOvalBg(hintPoint, msgPointColor);
        hintPoint.setLayoutParams(hintPointParams);

        //消息红点
        TextView msgPoint = itemView.findViewById(R.id.msg_point_tv);
        msgPoint.setTextSize(textSizeType, msgPointTextSize);
        RelativeLayout.LayoutParams msgPointParams = (RelativeLayout.LayoutParams) msgPoint.getLayoutParams();
        msgPointParams.bottomMargin = (int) msgPointTop;
        msgPointParams.leftMargin = (int) msgPointLeft;
        msgPoint.setLayoutParams(msgPointParams);

        hintPointList.add(hintPoint);
        msgPointList.add(msgPoint);

        TextView text = itemView.findViewById(R.id.tab_text_tv);
        ImageView icon = itemView.findViewById(R.id.tab_icon_iv);

        switch (contentType) {
            case TabContentType.TYPE_ONLY_IMAGE:
                text.setVisibility(GONE);
                icon.setScaleType(scaleType);
                LayoutParams iconParams = (LayoutParams) icon.getLayoutParams();
                iconParams.width = iconSize;
                iconParams.height = iconSize;
                icon.setLayoutParams(iconParams);
                imageViewList.add(icon);
                icon.setVisibility(VISIBLE);
                break;
            case TabContentType.TYPE_ONLY_TEXT:
                textViewList.add(text);
                LayoutParams textParams = (LayoutParams) text.getLayoutParams();
                textParams.topMargin = 0;
                text.setLayoutParams(textParams);
                text.setText(titleItems[position]);
                text.setTextSize(textSizeType, tabTextSize);
                text.setVisibility(VISIBLE);
                icon.setVisibility(GONE);
                break;
            default:
                textViewList.add(text);
                LayoutParams textParams2 = (LayoutParams) text.getLayoutParams();
                textParams2.topMargin = (int) tabTextTop;
                text.setLayoutParams(textParams2);
                text.setText(titleItems[position]);
                text.setTextSize(textSizeType, tabTextSize);

                icon.setScaleType(scaleType);
                LayoutParams iconParams2 = (LayoutParams) icon.getLayoutParams();
                iconParams2.width = iconSize;
                iconParams2.height = iconSize;
                icon.setLayoutParams(iconParams2);
                imageViewList.add(icon);

                text.setVisibility(VISIBLE);
                icon.setVisibility(VISIBLE);
                break;
        }

        tabList.add(itemView);
        navigationLayout.addView(itemView);
    }

    /**
     * 添加中间view的布局
     */
    private void addCenterTabView(int index) {
        RelativeLayout addItemView = new RelativeLayout(getContext());
        RelativeLayout.LayoutParams addItemParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        addItemParams.width = getWidth() / (tabCount + 1);
        addItemView.setLayoutParams(addItemParams);
        navigationLayout.addView(addItemView);

        final LinearLayout centerLinearLayout = new LinearLayout(getContext());
        centerLinearLayout.setOrientation(VERTICAL);
        centerLinearLayout.setGravity(Gravity.CENTER);
        final RelativeLayout.LayoutParams linearParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        centerImage = new ImageView(getContext());
        LayoutParams imageParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (centerIconSize > 0) {
            imageParams.width = (int) centerIconSize;
            imageParams.height = (int) centerIconSize;
        }
        centerImage.setLayoutParams(imageParams);

        if (centerLayoutRule == RULE_CENTER) {
            linearParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        } else if (centerLayoutRule == RULE_BOTTOM) {
            linearParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            linearParams.addRule(RelativeLayout.ABOVE, R.id.empty_line);
            if (centerAlignBottom) {
                if (textViewList != null && textViewList.size() > 0) {
                    textViewList.get(0).post(() -> {
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) empty_line.getLayoutParams();
                        params.height = (int) ((navigationHeight - textViewList.get(0).getHeight() - iconSize - tabTextTop) / 2);
                        empty_line.setLayoutParams(params);
                        //linearParams.bottomMargin = (int) ((navigationHeight - textViewList.get(0).getHeight() - iconSize - tabTextTop) / 2);
                    });
                }
            } else {
                linearParams.bottomMargin = (int) centerLayoutBottomMargin;
            }
        }

        centerImage.setId(-1);
        centerImage.setImageResource(centerImageRes);
        centerImage.setOnClickListener(view -> {
            if (onCenterTabClickListener != null) {
                if (!onCenterTabClickListener.onCenterTabSelectEvent(view)) {
                    if (centerAsFragment) {
                        selectTab(tabCount / 2, smoothScroll);
                    }
                }
            } else {
                if (centerAsFragment) {
                    selectTab(tabCount / 2, smoothScroll);
                }
            }
        });

        centerLinearLayout.addView(centerImage);

        if (!TextUtils.isEmpty(centerTextStr)) {
            TextView centerText = new TextView(getContext());
            centerText.setTextSize(textSizeType, centerTextSize);
            LayoutParams addTextParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            addTextParams.topMargin = (int) centerTextTopMargin;
            centerText.setLayoutParams(addTextParams);
            centerText.setText(centerTextStr);
            centerLinearLayout.addView(centerText);
        }

        addContainerLayout.addView(centerLinearLayout, linearParams);
    }

    /**
     * 添加自定义view到导航中间布局
     */
    private void addCenterTabCustomView(int i) {
        RelativeLayout addItemView = new RelativeLayout(getContext());
        RelativeLayout.LayoutParams addItemParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        addItemParams.width = getWidth() / (tabCount + 1);
        addItemView.setLayoutParams(addItemParams);
        navigationLayout.addView(addItemView);

        final RelativeLayout.LayoutParams linearParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if (centerLayoutRule == RULE_CENTER) {
            linearParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        } else if (centerLayoutRule == RULE_BOTTOM) {
            linearParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            if (centerAlignBottom) {
                linearParams.addRule(RelativeLayout.ABOVE, R.id.empty_line);
                if (textViewList != null && textViewList.size() > 0) {
                    textViewList.get(0).post(() -> linearParams.bottomMargin = (int) ((navigationHeight - textViewList.get(0).getHeight() - iconSize - tabTextTop) / 2));
                }
            } else {
                linearParams.addRule(RelativeLayout.ABOVE, R.id.empty_line);
                linearParams.bottomMargin = (int) centerLayoutBottomMargin;
            }
        }
        if (customAddView == null) {
            return;
        }
        customAddView.setId(-1);
        customAddView.setOnClickListener(view -> {
            if (onCenterTabClickListener != null) {
                if (!onCenterTabClickListener.onCenterTabSelectEvent(view)) {
                    if (centerAsFragment) {
                        selectTab(tabCount / 2, smoothScroll);
                    }
                }
            } else {
                if (centerAsFragment) {
                    selectTab(tabCount / 2, smoothScroll);
                }
            }
        });

        addContainerLayout.addView(customAddView, linearParams);
    }

    /**
     * 切换ViewPager页面
     */
    public void selectTab(int position, boolean smoothScroll) {
        selectTab(position, smoothScroll, true);
    }

    /**
     * 切换ViewPager页面
     */
    public void selectTab(int position, boolean smoothScroll, boolean selectPager) {
        if (currentPosition == position) {
            return;
        }
        if (position >= tabCount) {
            return;
        }
        currentPosition = position;
        if (selectPager) {
            if (getViewPager() != null) {
                getViewPager().setCurrentItem(position, smoothScroll);
            }
        }
        updateNavigation(true);
    }
    
    /**
     * 更新导航栏图标
     */
    public void updateNavigationIcon(int position, boolean isNormal, int res) {
        if (isNormal) {
            if (normalIconItems == null | position >= (normalIconItems != null ? normalIconItems.length : 0)) {
                return;
            }
            normalIconItems[position] = res;
        } else {
            if (selectIconItems == null | position >= (selectIconItems != null ? selectIconItems.length : 0)) {
                return;
            }
            selectIconItems[position] = res;
        }
        updateNavigation(false);
    }

    /**
     * 更新导航栏文字
     */
    public void updateNavigationText(int position, boolean isNormal, String str) {
        if (titleItems == null || position >= titleItems.length) {
            return;
        }
        titleItems[position] = str;
        updateNavigation(false);
    }

    /**
     * 更新导航栏UI
     */
    public void updateNavigation(boolean showAnim) {
        //如果有加号标签
        if (isCenterAsFragment()) {
            //是不是中心位置
            if (isCenterPosition(currentPosition)) {
                selectCenterTabUI();
            } else if (isBeforeCenter(currentPosition)) {
                selectNormalTabUI(currentPosition, showAnim);
            } else {
                selectNormalTabUI(currentPosition - 1, showAnim);
            }
        } else {
            selectNormalTabUI(currentPosition, showAnim);
        }
    }

    /**
     * 是否是前面位置
     */
    private boolean isBeforeCenter(int position) {
        return position < (tabCount / 2);
    }

    /**
     * 是否是中间位置
     */
    private boolean isCenterPosition(int position) {
        return position == tabCount / 2;
    }

    /**
     * 选择中间Tab UI变化
     */
    private void selectCenterTabUI() {
        for (int i = 0; i < tabCount; i++) {
            switch (contentType) {
                case TabContentType.TYPE_NORMAL:
                    imageViewList.get(i).setImageResource(normalIconItems[i]);
                    textViewList.get(i).setTextColor(normalTextColor);
                    textViewList.get(i).setText(titleItems[i]);
                case TabContentType.TYPE_ONLY_IMAGE:
                    imageViewList.get(i).setImageResource(normalIconItems[i]);
                    break;
                case TabContentType.TYPE_ONLY_TEXT:
                    textViewList.get(i).setTextColor(normalTextColor);
                    textViewList.get(i).setText(titleItems[i]);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 选择普通tab UI变化
     */
    private void selectNormalTabUI(int position, boolean showAnim) {
        if (imageViewList == null || imageViewList.size() == 0) {
            return;
        }
        if (textViewList == null || textViewList.size() == 0) {
            return;
        }
        for (int i = 0; i < tabCount; i++) {
            if (i == position) {
//                if (anim != null && showAnim)
//                    YoYo.with(anim).duration(300).playOn(tabList.get(i));
                switch (contentType) {
                    case TabContentType.TYPE_NORMAL:
                        imageViewList.get(i).setImageResource(selectIconItems[i]);
                        textViewList.get(i).setTextColor(selectTextColor);
                        textViewList.get(i).setText(titleItems[i]);
                        break;
                    case TabContentType.TYPE_ONLY_IMAGE:
                        imageViewList.get(i).setImageResource(selectIconItems[i]);
                        break;
                    case TabContentType.TYPE_ONLY_TEXT:
                        textViewList.get(i).setTextColor(selectTextColor);
                        textViewList.get(i).setText(titleItems[i]);
                        break;
                    default:
                        break;
                }
            } else {
                switch (contentType) {
                    case TabContentType.TYPE_NORMAL:
                        imageViewList.get(i).setImageResource(normalIconItems[i]);
                        textViewList.get(i).setTextColor(normalTextColor);
                        textViewList.get(i).setText(titleItems[i]);
                    case TabContentType.TYPE_ONLY_IMAGE:
                        imageViewList.get(i).setImageResource(normalIconItems[i]);
                        break;
                    case TabContentType.TYPE_ONLY_TEXT:
                        textViewList.get(i).setTextColor(normalTextColor);
                        textViewList.get(i).setText(titleItems[i]);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @IntDef({
            TextSizeType.TYPE_DP,
            TextSizeType.TYPE_SP,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface TextSizeType {
        //文字单位：1、DP   2、SP
        int TYPE_DP = 1;
        int TYPE_SP = 2;
    }

    @IntDef({
            TabContentType.TYPE_NORMAL,
            TabContentType.TYPE_ONLY_IMAGE,
            TabContentType.TYPE_ONLY_TEXT,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface TabContentType {
        //Tab内容类型：0默认（有选中、未选中两种状态）  1仅图片  2仅文字
        int TYPE_NORMAL = 0;
        int TYPE_ONLY_IMAGE = 1;
        int TYPE_ONLY_TEXT = 2;
    }

    @IntDef({
            NavigationMode.MODE_NORMAL,
            NavigationMode.MODE_ADD,
            NavigationMode.MODE_ADD_VIEW,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface NavigationMode {
        //导航栏模式: 0默认, 1中心加号, 2中心自定义
        int MODE_NORMAL = 0;
        int MODE_ADD = 1;
        int MODE_ADD_VIEW = 2;
    }

    /**
     * 设置是否显示小红点
     *
     * @param position 第几个tab
     * @param isShow   是否显示
     */
    public void setHintPoint(int position, boolean isShow) {
        if (getWidth() <= 0 || getHeight() <= 0) {
            return;
        }
        if (hintPointList == null || hintPointList.size() < (position + 1)) {
            return;
        }
        if (isShow) {
            hintPointList.get(position).setVisibility(VISIBLE);
        } else {
            hintPointList.get(position).setVisibility(GONE);
        }
    }

    /**
     * 设置消息数量
     *
     * @param position 第几个tab
     * @param count    显示的数量  99个以上显示99+  少于1则不显示
     */
    public void setMsgPointCount(int position, int count) {
        if (msgPointList == null || msgPointList.size() < (position + 1)) {
            return;
        }
        TextView msgPointView = msgPointList.get(position);
        if (count > 99) {
            NavigationUtil.setRoundRectBg(getContext(), msgPointView, msgPointMoreRadius, msgPointColor);
            msgPointView.setText("99+");
            ViewGroup.LayoutParams params = msgPointView.getLayoutParams();
            params.width = (int) msgPointMoreWidth;
            params.height = (int) msgPointMoreHeight;
            msgPointView.setLayoutParams(params);
            msgPointView.setVisibility(VISIBLE);
        } else if (count < 1) {
            msgPointView.setVisibility(GONE);
        } else {
            ViewGroup.LayoutParams params = msgPointView.getLayoutParams();
            params.width = (int) msgPointSize;
            params.height = (int) msgPointSize;
            msgPointView.setLayoutParams(params);
            NavigationUtil.setOvalBg(msgPointView, msgPointColor);
            msgPointView.setText(count + "");
            msgPointView.setVisibility(VISIBLE);
        }
    }

    /**
     * 清除提示红点
     */
    public void clearHintPoint(int position) {
        if (hintPointList == null || hintPointList.size() < (position + 1)) {
            return;
        }
        hintPointList.get(position).setVisibility(GONE);
    }

    /**
     * 清空所有提示红点
     */
    public void clearAllHintPoint() {
        for (int i = 0; i < hintPointList.size(); i++) {
            hintPointList.get(i).setVisibility(GONE);
        }
    }

    /**
     * 清除数字消息
     */
    public void clearMsgPoint(int position) {
        if (msgPointList == null || msgPointList.size() < (position + 1)) {
            return;
        }
        msgPointList.get(position).setVisibility(GONE);
    }
    
    /**
     * 清空所有数字消息红点
     */
    public void clearAllMsgPoint() {
        for (int i = 0; i < msgPointList.size(); i++) {
            msgPointList.get(i).setVisibility(GONE);
        }
    }

    public interface OnTabClickListener {
        boolean onTabSelectEvent(View view, int position);

        /**
         * 重复点击
         */
        boolean onTabReSelectEvent(View view, int position);
    }

    public interface OnCenterTabSelectListener {
        /**
         * 中间布局点击事件
         */
        boolean onCenterTabSelectEvent(View view);
    }

    public interface OnTabLoadListener {
        /**
         * Tab加载完毕
         */
        void onTabLoadCompleteEvent();
    }

    
    /**
     *  set/get方法
     */
    public EasyNavigationBar titleItems(String[] titleItems) {
        this.titleItems = titleItems;
        return this;
    }

    public String[] getTitleItems() {
        return titleItems;
    }

    public EasyNavigationBar normalIconItems(int[] normalIconItems) {
        this.normalIconItems = normalIconItems;
        return this;
    }
    
    public int[] getNormalIconItems() {
        return normalIconItems;
    }
    
    public EasyNavigationBar selectIconItems(int[] selectIconItems) {
        this.selectIconItems = selectIconItems;
        return this;
    }

    public int[] getSelectIconItems() {
        return selectIconItems;
    }

    public EasyNavigationBar fragmentList(List<Fragment> fragmentList) {
        this.fragmentList = fragmentList;
        return this;
    }
    
    public List<Fragment> getFragmentList() {
        return fragmentList;
    }

    public EasyNavigationBar fragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        return this;
    }

    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    public ViewPagerAdapter getAdapter() {
        return adapter;
    }

    public RelativeLayout getContentView() {
        return contentView;
    }
    
    public void setAddViewLayout(View addViewLayout) {
        FrameLayout.LayoutParams addParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.addViewLayout.addView(addViewLayout, addParams);
    }

    public RelativeLayout getAddContainerLayout() {
        return addContainerLayout;
    }

    public LinearLayout getNavigationLayout() {
        return navigationLayout;
    }

    public View getLineView() {
        return lineView;
    }

    public ViewGroup getAddLayout() {
        return addViewLayout;
    }
    
    public ViewGroup getAddViewLayout() {
        return addViewLayout;
    }

    public List<ImageView> getImageViewList() {
        return imageViewList;
    }

    public List<TextView> getTextViewList() {
        return textViewList;
    }

    public List<View> getTabList() {
        return tabList;
    }
    
    public ViewPager getViewPager() {
        return mViewPager;
    }

//    public Techniques getAnim() {
//        return anim;
//    }

//    public EasyNavigationBar anim(Anim anim) {
//        if (anim != null) {
//            this.anim = anim.getYoyo();
//        } else {
//            this.anim = null;
//        }
//        return this;
//    }

    public EasyNavigationBar smoothScroll(boolean smoothScroll) {
        this.smoothScroll = smoothScroll;
        return this;
    }

    public boolean isSmoothScroll() {
        return smoothScroll;
    }
    
    public EasyNavigationBar navigationBackground(int navigationBackground) {
        this.navigationBackground = navigationBackground;
        return this;
    }

    public int getNavigationBackground() {
        return navigationBackground;
    }

    public EasyNavigationBar navigationHeight(int navigationHeight) {
        this.navigationHeight = NavigationUtil.dip2px(getContext(), navigationHeight);
        return this;
    }

    public float getNavigationHeight() {
        return navigationHeight;
    }

    public EasyNavigationBar canScroll(boolean canScroll) {
        this.canScroll = canScroll;
        return this;
    }

    public boolean isCanScroll() {
        return canScroll;
    }
    
    public EasyNavigationBar mode(int mode) {
        this.mode = mode;
        return this;
    }

    public int getMode() {
        return mode;
    }

    public EasyNavigationBar hasPadding(boolean hasPadding) {
        this.hasPadding = hasPadding;
        return this;
    }

    public boolean isHasPadding() {
        return hasPadding;
    }
    
    public int getCurrentPosition() {
        return currentPosition;
    }

    public EasyNavigationBar iconSize(float iconSize) {
        this.iconSize = NavigationUtil.dip2px(getContext(), iconSize);
        return this;
    }

    public int getIconSize() {
        return iconSize;
    }
    
    public EasyNavigationBar scaleType(ImageView.ScaleType scaleType) {
        this.scaleType = scaleType;
        return this;
    }

    public ImageView.ScaleType getScaleType() {
        return scaleType;
    }

    public EasyNavigationBar tabTextTop(int tabTextTop) {
        this.tabTextTop = NavigationUtil.dip2px(getContext(), tabTextTop);
        return this;
    }

    public float getTabTextTop() {
        return tabTextTop;
    }

    public EasyNavigationBar tabTextSize(int tabTextSize) {
        this.tabTextSize = tabTextSize;
        return this;
    }

    public float getTabTextSize() {
        return tabTextSize;
    }
    
    public EasyNavigationBar textSizeType(int textSizeType) {
        this.textSizeType = textSizeType;
        return this;
    }
    
    public int getTextSizeType() {
        return textSizeType;
    }

    public EasyNavigationBar normalTextColor(int normalTextColor) {
        this.normalTextColor = normalTextColor;
        return this;
    }

    public int getNormalTextColor() {
        return normalTextColor;
    }

    public EasyNavigationBar selectTextColor(int selectTextColor) {
        this.selectTextColor = selectTextColor;
        return this;
    }

    public int getSelectTextColor() {
        return selectTextColor;
    }

    public EasyNavigationBar hintPointSize(float hintPointSize) {
        this.hintPointSize = NavigationUtil.dip2px(getContext(), hintPointSize);
        return this;
    }

    public float getHintPointSize() {
        return hintPointSize;
    }

    public EasyNavigationBar hintPointLeft(int hintPointLeft) {
        this.hintPointLeft = NavigationUtil.dip2px(getContext(), hintPointLeft);
        return this;
    }

    public float getHintPointLeft() {
        return hintPointLeft;
    }

    public EasyNavigationBar hintPointTop(int hintPointTop) {
        this.hintPointTop = NavigationUtil.dip2px(getContext(), hintPointTop);
        return this;
    }

    public float getHintPointTop() {
        return hintPointTop;
    }

    public EasyNavigationBar msgPointTextSize(int msgPointTextSize) {
        this.msgPointTextSize = msgPointTextSize;
        return this;
    }

    public float getMsgPointTextSize() {
        return msgPointTextSize;
    }

    public EasyNavigationBar msgPointSize(float msgPointSize) {
        this.msgPointSize = NavigationUtil.dip2px(getContext(), msgPointSize);
        return this;
    }

    public float getMsgPointSize() {
        return msgPointSize;
    }

    public EasyNavigationBar setMsgPointMoreWidth(float msgPointMoreWidth) {
        this.msgPointMoreWidth = NavigationUtil.dip2px(getContext(), msgPointMoreWidth);
        return this;
    }

    public float getMsgPointMoreWidth() {
        return msgPointMoreWidth;
    }

    public EasyNavigationBar setMsgPointMoreHeight(float msgPointMoreHeight) {
        this.msgPointMoreHeight = NavigationUtil.dip2px(getContext(), msgPointMoreHeight);
        return this;
    }

    public float getMsgPointMoreHeight() {
        return msgPointMoreHeight;
    }

    public EasyNavigationBar setMsgPointMoreRadius(int msgPointMoreRadius) {
        this.msgPointMoreRadius = msgPointMoreRadius;
        return this;
    }

    public float getMsgPointMoreRadius() {
        return msgPointMoreRadius;
    }

    public EasyNavigationBar setMsgPointColor(int msgPointColor) {
        this.msgPointColor = msgPointColor;
        return this;
    }

    public int getMsgPointColor() {
        return msgPointColor;
    }
    
    public EasyNavigationBar msgPointLeft(int msgPointLeft) {
        this.msgPointLeft = NavigationUtil.dip2px(getContext(), msgPointLeft);
        return this;
    }

    public float getMsgPointLeft() {
        return msgPointLeft;
    }

    public EasyNavigationBar msgPointTop(int msgPointTop) {
        this.msgPointTop = NavigationUtil.dip2px(getContext(), msgPointTop);
        return this;
    }

    public float getMsgPointTop() {
        return msgPointTop;
    }

    public EasyNavigationBar lineHeight(int lineHeight) {
        this.lineHeight = lineHeight;
        return this;
    }

    public float getLineHeight() {
        return lineHeight;
    }

    public EasyNavigationBar lineColor(int lineColor) {
        this.lineColor = lineColor;
        return this;
    }

    public int getLineColor() {
        return lineColor;
    }

    public EasyNavigationBar centerIconSize(float centerIconSize) {
        this.centerIconSize = NavigationUtil.dip2px(getContext(), centerIconSize);
        return this;
    }

    public float getCenterIconSize() {
        return centerIconSize;
    }
    
    public EasyNavigationBar centerLayoutHeight(int centerLayoutHeight) {
        this.centerLayoutHeight = NavigationUtil.dip2px(getContext(), centerLayoutHeight);
        return this;
    }

    public float getCenterLayoutHeight() {
        return centerLayoutHeight;
    }

    public EasyNavigationBar centerLayoutBottomMargin(int centerLayoutBottomMargin) {
        this.centerLayoutBottomMargin = NavigationUtil.dip2px(getContext(), centerLayoutBottomMargin);
        return this;
    }

    public float getCenterLayoutBottomMargin() {
        return centerLayoutBottomMargin;
    }

    public EasyNavigationBar centerLayoutRule(int centerLayoutRule) {
        this.centerLayoutRule = centerLayoutRule;
        return this;
    }

    public int getCenterLayoutRule() {
        return centerLayoutRule;
    }

    public EasyNavigationBar centerAsFragment(boolean centerAsFragment) {
        this.centerAsFragment = centerAsFragment;
        return this;
    }

    public boolean isCenterAsFragment() {
        return centerAsFragment && isAddPage();
    }

    public EasyNavigationBar addCustomView(View customAddView) {
        this.customAddView = customAddView;
        return this;
    }

    public View getCustomAddView() {
        return customAddView;
    }

    public EasyNavigationBar centerTextSize(int centerTextSize) {
        this.centerTextSize = NavigationUtil.dip2px(getContext(), centerTextSize);
        return this;
    }

    public float getCenterTextSize() {
        return centerTextSize;
    }

    public EasyNavigationBar centerNormalTextColor(int centerNormalTextColor) {
        this.centerNormalTextColor = centerNormalTextColor;
        return this;
    }

    public int getCenterNormalTextColor() {
        return centerNormalTextColor;
    }

    public EasyNavigationBar centerSelectTextColor(int centerSelectTextColor) {
        this.centerSelectTextColor = centerSelectTextColor;
        return this;
    }

    public int getCenterSelectTextColor() {
        return centerSelectTextColor;
    }

    public EasyNavigationBar centerTextTopMargin(int centerTextTopMargin) {
        this.centerTextTopMargin = NavigationUtil.dip2px(getContext(), centerTextTopMargin);
        return this;
    }

    public float getCenterTextTopMargin() {
        return centerTextTopMargin;
    }

    public EasyNavigationBar centerAlignBottom(boolean centerAlignBottom) {
        this.centerAlignBottom = centerAlignBottom;
        return this;
    }

    public boolean isCenterAlignBottom() {
        return centerAlignBottom;
    }

    public ImageView getCenterImage() {
        return centerImage;
    }
    
    public EasyNavigationBar centerImageRes(int centerImageRes) {
        this.centerImageRes = centerImageRes;
        return this;
    }

    public EasyNavigationBar centerTextStr(String centerTextStr) {
        this.centerTextStr = centerTextStr;
        return this;
    }
    
    public EasyNavigationBar setOnTabClickListener(OnTabClickListener onTabClickListener) {
        this.onTabClickListener = onTabClickListener;
        return this;
    }

    public OnTabClickListener getOnTabClickListener() {
        return onTabClickListener;
    }

    public EasyNavigationBar setOnCenterTabClickListener(OnCenterTabSelectListener onCenterTabClickListener) {
        this.onCenterTabClickListener = onCenterTabClickListener;
        return this;
    }

    public EasyNavigationBar setOnTabLoadListener(OnTabLoadListener onTabLoadListener) {
        this.onTabLoadListener = onTabLoadListener;
        return this;
    }

}
