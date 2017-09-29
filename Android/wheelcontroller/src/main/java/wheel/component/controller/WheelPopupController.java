/* * Copyright (C) 2010 The Android Open Source Project * * Licensed under the Apache License, Version 2.0 (the "License"); * you may not use this file except in compliance with the License. * You may obtain a copy of the License at * *      http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */package wheel.component.controller;import wheel.component.genview.GenWheelText;import wheel.component.genview.GenWheelView;import wheel.component.genview.UnSupportedWheelViewException;import wheel.component.genview.WheelGeneralAdapter;import wheel.component.utils.UIAdjuster;import wheel.component.utils.WheelUtility;import wheel.component.view.TriangleView;import wheel.component.view.WheelControlListener;import kankan.wheel.widget.OnWheelChangedListener;import kankan.wheel.widget.OnWheelScrollListener;import kankan.wheel.widget.WheelView;import mma.mtake.wheel.component.R;import android.content.Context;import android.graphics.Color;import android.graphics.drawable.ColorDrawable;import android.os.Message;import android.util.SparseArray;import android.view.Display;import android.view.KeyEvent;import android.view.LayoutInflater;import android.view.MotionEvent;import android.view.View;import android.view.View.OnKeyListener;import android.view.View.OnTouchListener;import android.view.WindowManager;import android.widget.LinearLayout;import android.widget.PopupWindow;import android.widget.TextView;/****** *  * 利用PopupWindow 動態的顯示滾輪視窗。</br> 請用WheelControlListener 接收callback與選擇的結果</br> * OnShowWheelListenr --->可以在滾輪顯示之前，做一些動作，如提示視窗。</br> *  * *Be careful!!!. Depends on the Android internal policy,</br> you can not * leave PopupWindow open while Activity is being finished.You have to close * this PopupWindow before Activity to be finished.Otherwise, the only * consequences you will receive that Application is terminated</br></br> *  * PopupWindow 一定要在 Activity 被finish之前關起來，不然程式會crash *  * <pre class="prettyprint"> * 使用範例 * 	 * 	private TextView test_left; * 	private TextView test_left_bottom; * 	private TextView test_right; * 	private TextView test_top; * 	private String[] data_test_left; * 	private String[] data_left_bottom; * 	private String[] data_right; * 	private String[] data_top; *  * 	private int[] drawableArray = { R.drawable.canada, R.drawable.france, R.drawable.ukraine, R.drawable.usa }; * 	private String[] citys = { "Canada", "France", "Ukraine", "Usa" }; *  * test_left.setText("General Wheel Adapter"); * 		test_right.setText("Custom Wheel Adapter"); * 		 * 		data_test_left = new String[20]; * 		data_left_bottom = new String[20]; * 		data_right = new String[20]; * 		data_top = new String[20]; * 		for (int i = 0; i < 20; i++) { * 			data_test_left[i] = "data_test_left_" + i; * 			data_left_bottom[i] = "data_left_bottom_" + i; * 			data_right[i] = "data_right_" + i; * 			data_top[i] = "data_top_" + i; * 		} * 		WheelPopupController controller = new WheelPopupController(this, listener); * 		controller.setWheelListener(test_left, data_test_left); * 		controller.setWheelListener(test_left_bottom, data_left_bottom); * 		controller.setWheelListener(test_top, data_top); *  * 		WheelPopupController controller2 = new WheelPopupController(this, listener, new GenView()); * 		controller2.setWheelListener(test_right, citys); *  * private class GenView extends GenWheelView { * 		@Override * 		protected View genBody(Context context, View convertView, Object element, int position) { * 			View body = getLayoutInflater().inflate(R.layout.custom_wheel_inner, null); * 			ImageView icn = (ImageView) body.findViewById(R.id.icon); * 			TextView text = (TextView) body.findViewById(R.id.text); * 			icn.setBackgroundDrawable(getResources().getDrawable(drawableArray[position])); * 			text.setText(element.toString()); * 			return body; * 		} * 	} *  * 	private WheelControlListener listener = new WheelControlListener() { * 		@Override * 		public void handleClick(int viewId, Object obj) { * 			if (viewId == R.id.test_left) { * 				test_left.setText(obj.toString()); * 			} else if (viewId == R.id.test_right) { * 				test_right.setText(obj.toString()); * 			} else if (viewId == R.id.test_top) { * 				test_top.setText(obj.toString()); * 			} * 		} * 	}; *  * </pre> *  * @author JosephWang */public class WheelPopupController implements OnKeyListener {	private PopupWindow popWindow;// 提示視窗的	public static final String TAG = WheelPopupController.class.getSimpleName();	@SuppressWarnings("rawtypes")	private WheelControlListener controllerListenr;	private Context activity;	private WheelView wheelView;	private TextView titleView;	private SparseArray<Object> collection = new  SparseArray<Object>();		private TriangleView left_top_triangle;	private TriangleView center_top_triangle;	private TriangleView right_top_triangle;	private TriangleView left_bottom_triangle;	private TriangleView center_bottom_triangle;	private TriangleView right_bottom_triangle;		private int deviceWidth = 0;	private int deviceHeight = 0;	private LinearLayout wheel;	private Message wheelMsg = new Message();	private int[] archorViewLocation = new int[2];	private GenWheelView genView;	private int currentClickViewId = 0;	private String titleText = "";	public String getTitleText() {		return titleText;	}	/**	 * 設置滾輪視窗的中間title	 * 	 * @param titleText	 */	public void setTitleText(String titleText) {		this.titleText = titleText;	}	private int index = 0;	private int moveCenterX;	private int moveCenterY;	private int dialogWidth = 300;	public int getDialogWidth() {		return dialogWidth;	}	/**	 * 設定Popup視窗寬度	 * 	 * @param dialogWidth	 */	public void setDialogWidth(int dialogWidth) {		this.dialogWidth = dialogWidth;	}	private int dialogHeight = 280;	public int getDialogHeight() {		return dialogHeight;	}	/**	 * 設定Popup視窗高度 預設275	 * 	 * @param dialogHeight	 */	public void setDialogHeight(int dialogHeight) {		this.dialogHeight = dialogHeight;	}	private int popWindowPositionX = 0;	/**	 * Popup視窗顯示的X軸位置	 * 	 * @param popWindowPositionX	 */	public int getPopWindowPositionX() {		return popWindowPositionX;	}	private int popWindowPositionY = 0;	/**	 * Popup視窗顯示的Y軸位置	 * 	 * @param popWindowPositionY	 */	public int getPopWindowPositionY() {		return popWindowPositionY;	}	/**	 * 設定顯示一般文字的滾輪	 * 	 * @param curr	 * @param controllerListenr	 * @param genView	 */	@SuppressWarnings("rawtypes")	public WheelPopupController(Context curr, WheelControlListener controllerListenr) {		this(curr, controllerListenr, null);	}	/**	 * 設定顯示特定樣式的滾輪	 * 	 * @param curr	 * @param controllerListenr	 * @param genView	 */	@SuppressWarnings("rawtypes")	public WheelPopupController(Context curr, WheelControlListener controllerListenr, GenWheelView genView) {		this.activity = curr;		this.controllerListenr = controllerListenr;		this.genView = genView;		initWheel();		initPopWindow();	}	/**	 * get The index in Current Select Data Collection.</br>	 * 	 * 返回目前所選資料集合的index	 * 	 * @return index	 */	public int getIndex() {		return index;	}	/**	 * get The size in Current Select Data Collection.</br>	 * 	 * 返回目前所選資料集合的大小	 * 	 * @return size	 * @throws IllegalAccessException 	 */		public int getDataSize(){		if (currentClickViewId > 0) {			return getDataSize(currentClickViewId);		} else {			return 0;		}	}		public int getDataSize(int viewId) {		return WheelUtility.getDataSize(collection.get(viewId));	}	public Object getDataByIndex(int index)	{		return getDataByIndex(currentClickViewId, index);	}		/********************	 * 依據index 取得物件	 * ***********************/	public Object getDataByIndex(int viewId, int index)  {		if (collection.size() > 0) {			return WheelUtility.getDataByIndex(collection.get(viewId), index);			} else {			return 0;		}	}	private boolean isTouchOutSideCancelable = true;	public boolean isTouchOutSideCancelable() {		return isTouchOutSideCancelable;	}	/**	 * 設定是否可以由外部點擊，來關閉poup視窗	 * 	 * @param isTouchOutSideCancelable	 */	public void setTouchOutSideCancelable(boolean isTouchOutSideCancelable) {		this.isTouchOutSideCancelable = isTouchOutSideCancelable;	}	private Object sArray;// 顯示在滾輪上的資料結構	/**	 * 取得當下滾輪資料集合	 * 	 * @return	 */	public Object getCurrentDataCollection() {		return sArray;	}	/**	 * 初始化視窗	 */	private void initPopWindow() {		if (popWindow == null) {			popWindow = new PopupWindow(wheel, (int) UIAdjuster.computeDIPtoPixel(activity, getDialogWidth()),											   (int) UIAdjuster.computeDIPtoPixel(activity, getDialogHeight()));			popWindow.setAnimationStyle(R.style.PopupArchorAnimation);			// popWindow.setBackgroundDrawable(null);//			// let_Animation_to_be_smooth!!!			/*** let_OutsideTouchable_to_be_work_for_closing_PopUpWindow!! *****/			popWindow.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));//			/*** let_OutsideTouchable_to_be_work_for_closing_PopUpWindow!! *****/			popWindow.setTouchable(true);			popWindow.setFocusable(true);			popWindow.setClippingEnabled(false);			popWindow.setOutsideTouchable(true);			popWindow.setTouchInterceptor(new OnTouchListener() {				@Override				public boolean onTouch(View v, MotionEvent event) {					switch (event.getAction()) {					case MotionEvent.ACTION_OUTSIDE:						if (isTouchOutSideCancelable()) {							dismiss();							return true;						} else {							return false;						}					}					return false;				}			});		}	}	/**	 * Close the WheelPopUpDateDialog.</br> 關閉滾輪	 * **/	public void dismiss() {		if (popWindow != null) {			popWindow.dismiss();		}	}	/**	 * 調整視窗位置	 * **/	private void adjustPopUpPosition(View anchor) {		left_top_triangle.setVisibility(View.GONE);		center_top_triangle.setVisibility(View.GONE);		right_top_triangle.setVisibility(View.GONE);		left_bottom_triangle.setVisibility(View.GONE);		center_bottom_triangle.setVisibility(View.GONE);		right_bottom_triangle.setVisibility(View.GONE);		if (!popWindow.isShowing()) {			/****************** 使用者點擊View 右邊寬度不足於放PopUpWindow 的調整 ***************************/			if (Math.abs(deviceWidth - moveCenterX) < Math.abs(deviceWidth - (int) UIAdjuster.computeDIPtoPixel(activity, getDialogWidth()))) {				popWindowPositionX = -(int) UIAdjuster.computeDIPtoPixel(activity, getDialogWidth());			} else {				popWindowPositionX = 0;			}			if (Math.abs(deviceHeight - moveCenterY) < (int) UIAdjuster.computeDIPtoPixel(activity, getDialogHeight())) {				popWindowPositionY = -(int) UIAdjuster.computeDIPtoPixel(activity, getDialogHeight()) - anchor.getHeight() + (int) UIAdjuster.computeDIPtoPixel(activity, 25) / 2;			} else {				popWindowPositionY = 0;			}			/****************** 使用者點擊在右下角 ***************************/			if (moveCenterX > deviceWidth / 2 && moveCenterY > deviceHeight / 2) {// 右下角				popWindowPositionX = -(int) UIAdjuster.computeDIPtoPixel(activity, getDialogWidth()) + anchor.getWidth();				popWindowPositionY = -(int) UIAdjuster.computeDIPtoPixel(activity, getDialogHeight()) - anchor.getHeight() + (int) UIAdjuster.computeDIPtoPixel(activity, 25) / 2;				left_top_triangle.setVisibility(View.GONE);				center_top_triangle.setVisibility(View.GONE);				right_top_triangle.setVisibility(View.GONE);				left_bottom_triangle.setVisibility(View.GONE);				center_bottom_triangle.setVisibility(View.GONE);				right_bottom_triangle.setVisibility(View.VISIBLE);			}			/****************** 使用者點擊在左下角 ***************************/			else if (moveCenterX < deviceWidth / 2 && moveCenterY > deviceHeight / 2) {// 左下角				popWindowPositionX = 0;				popWindowPositionY = -(int) UIAdjuster.computeDIPtoPixel(activity, getDialogHeight()) - anchor.getHeight() + (int) UIAdjuster.computeDIPtoPixel(activity, 25) / 2;				left_top_triangle.setVisibility(View.GONE);				center_top_triangle.setVisibility(View.GONE);				right_top_triangle.setVisibility(View.GONE);				left_bottom_triangle.setVisibility(View.VISIBLE);				center_bottom_triangle.setVisibility(View.GONE);				right_bottom_triangle.setVisibility(View.GONE);			}			/****************** 使用者點擊在右上角 ***************************/			else if (moveCenterX > deviceWidth / 2 && moveCenterY < deviceHeight / 2) {// 右上角				popWindowPositionX = -(int) UIAdjuster.computeDIPtoPixel(activity, getDialogWidth()) + anchor.getWidth();				popWindowPositionY = -(int) UIAdjuster.computeDIPtoPixel(activity, 25) / 2;				left_top_triangle.setVisibility(View.GONE);				center_top_triangle.setVisibility(View.GONE);				right_top_triangle.setVisibility(View.VISIBLE);				left_bottom_triangle.setVisibility(View.GONE);				center_bottom_triangle.setVisibility(View.GONE);				right_bottom_triangle.setVisibility(View.GONE);			}			/****************** 使用者點擊在左上角 ***************************/			else if (moveCenterX < deviceWidth / 2 && moveCenterY < deviceHeight / 2) {// 左上角				popWindowPositionX = 0;				popWindowPositionY = -(int) UIAdjuster.computeDIPtoPixel(activity, 25) / 2;				left_top_triangle.setVisibility(View.VISIBLE);				center_top_triangle.setVisibility(View.GONE);				right_top_triangle.setVisibility(View.GONE);				left_bottom_triangle.setVisibility(View.GONE);				center_bottom_triangle.setVisibility(View.GONE);				right_bottom_triangle.setVisibility(View.GONE);			} else {				left_top_triangle.setVisibility(View.GONE);				center_top_triangle.setVisibility(View.VISIBLE);				right_top_triangle.setVisibility(View.GONE);				left_bottom_triangle.setVisibility(View.GONE);				center_bottom_triangle.setVisibility(View.GONE);				right_bottom_triangle.setVisibility(View.GONE);			}			popWindow.showAsDropDown(anchor, popWindowPositionX, popWindowPositionY);			popWindowPositionX = 0;			popWindowPositionY = 0;		}	}	/**	 * 對個別的的View設定滾輪事件	 * 	 * @param eachView	 * @param collection	 *            (Only support List, Map,Object Array,Cursor,SparseArray	 *            ,SparseBooleanArray,SparseIntArray,Vector, and basic data	 *            type)	 * @param listener	 *            在滾輪顯示之前，可以做預設動作的callBack return true : 顯示滾輪視窗</br> return	 *            false : 不顯示滾輪視窗	 */	public void setWheelListener(View eachView, Object collection) {		eachView.setOnClickListener(getWheelListener(collection, 1));	}	/**	 * 對個別的的View設定滾輪事件	 * 	 * @param eachView	 * @param collection	 *            (Only support List, Map,Object Array,Cursor,SparseArray	 *            ,SparseBooleanArray,SparseIntArray,Vector, and basic data	 *            type)	 * @param line	 *            顯示文字行數	 */	public void setWheelListener(View eachView, Object collection, int line) {		eachView.setOnClickListener(getWheelListener(collection, line));	}	private View.OnClickListener getWheelListener(final Object data, final int line) {		return new View.OnClickListener() {			@Override			public void onClick(View v) {				currentClickViewId = v.getId();				UIAdjuster.closeKeyBoard(activity);				collection.put(v.getId(), data);				if (onShowWheelListener.showWheel(v)) {					index = 0;					wheelMsg.what = v.getId();					WheelUtility.setUpWheelSelectData(collection.get(v.getId()), wheelMsg);					changeInPutItems(v, data, line);				}			}		};	}	/**	 * 初始化滾輪視窗元件	 */	private void initWheel() {		wheel = (LinearLayout) LayoutInflater.from(activity).inflate(R.layout.wheel_popup, null);		wheel.setBackgroundColor(Color.argb(0, 0, 0, 0));		titleView = (TextView) wheel.findViewById(R.id.title);		wheelView = (WheelView) wheel.findViewById(R.id.wheel_view);		wheelView.addScrollingListener(scrollListener);		wheelView.addChangingListener(changedListener);		wheel.findViewById(R.id.ok).setOnClickListener(buttonClickListener);		wheel.findViewById(R.id.cancel).setOnClickListener(buttonClickListener);		left_top_triangle = (TriangleView) wheel.findViewById(R.id.left_top_triangle);		center_top_triangle = (TriangleView) wheel.findViewById(R.id.center_top_triangle);		right_top_triangle = (TriangleView) wheel.findViewById(R.id.right_top_triangle);		left_bottom_triangle = (TriangleView) wheel.findViewById(R.id.left_bottom_triangle);		center_bottom_triangle = (TriangleView) wheel.findViewById(R.id.center_bottom_triangle);		right_bottom_triangle = (TriangleView) wheel.findViewById(R.id.right_bottom_triangle);	}	@SuppressWarnings("deprecation")	private void changeInPutItems(View anchor, Object data, int line) {		titleView.setText("" + getTitleText());		if (deviceHeight == 0 || deviceWidth == 0) {			Display display = ((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();			deviceHeight = display.getHeight();			deviceWidth = display.getWidth();		}		anchor.getLocationInWindow(archorViewLocation);		moveCenterX = archorViewLocation[0];		moveCenterY = archorViewLocation[1];		if (collection.get(anchor.getId()) == null) {			sArray = new Object();			collection.put(anchor.getId(), sArray);		}		sArray = collection.get(anchor.getId());				index = 0;		wheelView.setCurrentItem(0);		if (genView == null) {			genView = new GenWheelText(line, textSize);		}		WheelGeneralAdapter adapter = new WheelGeneralAdapter(activity, genView);		try {			adapter.setData(sArray);		} catch (UnSupportedWheelViewException e) {			e.printStackTrace();		}		wheelView.setViewAdapter(adapter);		adjustPopUpPosition(anchor);	}	private OnWheelChangedListener changedListener = new OnWheelChangedListener() {		@Override		public void onChanged(WheelView wheel, int oldValue, int newValue) {			index = newValue;			wheelMsg.obj = WheelUtility.getDataByIndex(sArray, index);		}	};	private View.OnClickListener buttonClickListener = new View.OnClickListener() {		@SuppressWarnings("unchecked")		@Override		public void onClick(View v) {			if (v.getId() == R.id.ok) {				if (isScrollFinish()) {					index = wheelView.getCurrentItem();					wheelMsg.obj = WheelUtility.getDataByIndex(sArray, index);					controllerListenr.handleClick(wheelMsg.what, wheelMsg.obj);					dismiss();				}			} else if (v.getId() == R.id.cancel) {				dismiss();			}		}	};	public Object getSelectData() {		return wheelMsg.obj;	}	private boolean isScrollFinish = true;	/**	 * 滾輪是否結束滾動的動畫	 * 	 * @return boolean	 */	public boolean isScrollFinish() {		return isScrollFinish;	}	private OnWheelScrollListener scrollListener = new OnWheelScrollListener() {		@Override		public void onScrollingStarted(WheelView wheel) {			isScrollFinish = false;		}		@Override		public void onScrollingFinished(WheelView wheel) {			isScrollFinish = true;			index = wheel.getCurrentItem();			wheelMsg.obj = WheelUtility.getDataByIndex(sArray, index);		}	};	private int textSize = 22;	public int getTextSize() {		return textSize;	}	/**	 * 設定顯示在滾輪的字體大小 in dip，預設22dip	 * 	 * @param textSize	 */	public void setTextSize(int textSize) {		this.textSize = textSize;	}	private OnShowWheelListener onShowWheelListener = new OnShowWheelListener() {		@Override		public boolean showWheel(View v) {			return true;		}	};	public OnShowWheelListener getOnShowWheelListenr() {		return onShowWheelListener;	}	/**	 * 設定在滾輪顯示之前，可以做預設動作的callBack return true : 顯示滾輪視窗</br> return false :	 * 不顯示滾輪視窗	 * 	 * @author JosephWang	 * 	 */	public void setOnShowWheelListenr(OnShowWheelListener onShowWheelListenr) {		this.onShowWheelListener = onShowWheelListenr;	}	@Override	public boolean onKey(View v, int keyCode, KeyEvent event) {		dismiss();		return onKey(v, keyCode, event);	}}