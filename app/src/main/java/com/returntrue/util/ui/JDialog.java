package com.returntrue.util.ui;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.returntrue.R;
import com.returntrue.framework.JActivity;


/**
 * Central dialog-create factory
 * 
 * @author JosephWang
 * 
 */
public class JDialog
{
	public static boolean notShowing(JActivity act, Dialog dialog)
	{
		return act != null &&
				!act.isFinishing() &&
				dialog != null &&
				!dialog.isShowing();
	}

	/**
	 * show loading progress
	 * 
	 * @param context
	 * @param msg
	 * @param cancelable
	 * @return {@link ProgressDialog}
	 */
	public static ProgressDialog showProgressDialog(Context context, String msg, boolean cancelable) {
		ProgressDialog p = new ProgressDialog(context, R.style.ProgressDialogSlide);
		p.requestWindowFeature(Window.FEATURE_NO_TITLE);
		p.getWindow().setBackgroundDrawableResource(R.color.transparent);
		p.setIndeterminate(true);
		p.setCancelable(cancelable);
		p.setMessage(msg);
		p.show();
		View content = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null);
		p.setContentView(content);
		((TextView) content.findViewById(R.id.progressTitle)).setText("" + msg);

		avoidDismiss(p, false);
		return p;
	}

	/**
	 * show message with one button
	 *
	 * @param context
	 * @param title
	 * @param msg
	 * @return {@link Dialog}
	 */
	public static Dialog showMessage(Context context, int title, int msg) {
		return showMessage(context, title, msg, null);
	}

	public static void showToast(Context context, int title) {
		Toast.makeText(context, context.getString(title), Toast.LENGTH_LONG).show();
	}

	public static Dialog showMessage(Context context, String title, String msg) {
		return showMessage(context, title, msg, null);
	}

	public static Dialog showListDialog(Context context, int title, String[] msg, OnClickListener listener, DialogInterface.OnCancelListener cancelListener) {
		Builder builder = new Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
		builder.setItems(msg, listener);
		builder.setTitle(title);
		builder.setNegativeButton(android.R.string.cancel, listener);
		if (cancelListener != null) {
			builder.setOnCancelListener(cancelListener);
		}
		Dialog dialog = builder.create();
		dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
		dialog.show();
		return dialog;
	}

	public static Dialog showListDialog(Context context, int title, String[] msg, OnClickListener listener) {
		return showListDialog(context, title, msg, listener, null);
	}

	public static Dialog showListDialog(Context context, String title, String[] msg, OnClickListener listener) {
		// Builder builder = new AlertDialog.Builder(context,
		// R.style.AlertTheme);
		Builder builder = new Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
		builder.setItems(msg, listener);
		builder.setTitle(title);
		builder.setNegativeButton(android.R.string.cancel, listener);
		Dialog dialog = builder.create();
		dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
		dialog.show();
		return dialog;
	}

	/**
	 * show message with one button
	 *
	 * @param context
	 * @param title
	 * @param msg
	 * @param listener
	 * @return {@link Dialog}
	 */
	public static Dialog showMessage(Context context, int title, int msg, OnClickListener listener) {
		return showMessage(context, title, msg, listener, true);
	}

	public static Dialog showMessage(Context context, int title, int msg, OnClickListener listener, boolean cancelable) {
		Builder builder = new Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setPositiveButton(android.R.string.ok, listener);

		Dialog dialog = avoidDismiss(builder.create(), cancelable);
		dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
		dialog.show();

		return dialog;
	}

	public static Dialog showMessage(Context context, String title, String msg, OnClickListener listener) {
		return showMessage(context, title, msg, listener, null);
	}

	public static Dialog showMessage(Context context, int title, int msg, int confirmButtonText, OnClickListener listener, DialogInterface.OnDismissListener dismissListener) {
		String titleStr = context.getResources().getString(title);
		String msgStr = context.getResources().getString(msg);
		String confirmButtonTextStr = context.getResources().getString(confirmButtonText);
		return showMessage(context, titleStr, msgStr, confirmButtonTextStr, listener, dismissListener);
	}

	public static Dialog showMessage(Context context, String title, String msg, String confirmButtonText, OnClickListener listener, DialogInterface.OnDismissListener dismissListener) {
		Builder builder = new Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setPositiveButton(confirmButtonText, listener);
		if (dismissListener != null)
			builder.setOnDismissListener(dismissListener);

		Dialog dialog = avoidDismiss(builder.create(), false);
		dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
		dialog.show();

		return dialog;
	}

	public static Dialog showMessage(Context context, String title, String msg, String confirmButtonText, String cancelButtonText, OnClickListener listener) {
		return showMessage(context, title, msg, confirmButtonText, cancelButtonText, listener, null);
	}

	public static Dialog showMessage(Context context, int title, int msg, int confirmButtonText, int cancelButtonText, OnClickListener listener) {
		String titleStr = context.getResources().getString(title);
		String msgStr = context.getResources().getString(msg);
		String confirmButtonTextStr = context.getResources().getString(confirmButtonText);
		String cancelButtonTextStr = context.getResources().getString(cancelButtonText);
		return showMessage(context, titleStr, msgStr, confirmButtonTextStr, cancelButtonTextStr, listener, null);
	}

	public static Dialog showMessage(Context context, int title, int msg, int confirmButtonText, int cancelButtonText, OnClickListener listener, OnClickListener cancelListener) {
		String titleStr = context.getResources().getString(title);
		String msgStr = context.getResources().getString(msg);
		String confirmButtonTextStr = context.getResources().getString(confirmButtonText);
		String cancelButtonTextStr = context.getResources().getString(cancelButtonText);
		return showMessage(context, titleStr, msgStr, confirmButtonTextStr, cancelButtonTextStr, listener, cancelListener);
	}

	public static Dialog showMessage(Context context, String title, String msg, String confirmButtonText, String cancelButtonText, OnClickListener listener, OnClickListener cancelListener) {
		return showMessage(context, title, msg, confirmButtonText, cancelButtonText, listener, cancelListener, true);
	}

	public static Dialog showMessage(Context context, String title, String msg, String confirmButtonText, String cancelButtonText, OnClickListener listener, OnClickListener cancelListener,
			boolean cancelable) {
		Builder builder = new Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setPositiveButton(confirmButtonText, listener);
		if (cancelListener != null)
			builder.setNegativeButton(cancelButtonText, cancelListener);
		else {
			builder.setNegativeButton(cancelButtonText, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
		}
		Dialog dialog = avoidDismiss(builder.create(), cancelable);
		dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
		dialog.show();
		return dialog;
	}

	public static Dialog showMessage(Context context, String title, String msg, String confirmButtonText, OnClickListener listener) {
		return showMessage(context, title, msg, confirmButtonText, listener, null);
	}

	public static Dialog showMessage(Context context, String title, String msg, OnClickListener listener, DialogInterface.OnDismissListener dismissListener) {
		Builder builder = new Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setPositiveButton(android.R.string.ok, listener);
		if (dismissListener != null)
			builder.setOnDismissListener(dismissListener);
		Dialog dialog = avoidDismiss(builder.create(), true);
		dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
		dialog.show();
		return dialog;
	}

	public static Dialog showMessages(Context context, String title, String msg, String button) {
		Builder builder = new Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setPositiveButton(button, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		Dialog dialog = avoidDismiss(builder.create(), true);
		dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
		dialog.show();
		return dialog;
	}

	public static Dialog showMessageWithCancel(Context context, String title, String msg, OnClickListener listener) {
		Builder builder = new Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setPositiveButton(android.R.string.ok, listener);
		builder.setNegativeButton(android.R.string.cancel, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		Dialog dialog = avoidDismiss(builder.create(), true);
		dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
		dialog.show();
		return dialog;
	}

	public static Dialog showMessage(Context context, String title, String msg, OnClickListener listener, boolean isShowing) {
		Builder builder = new Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setPositiveButton(android.R.string.ok, listener);

		Dialog dialog = avoidDismiss(builder.create(), true);
		dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
		dialog.show();
		isShowing = dialog.isShowing();
		return dialog;
	}

	/**
	 * show message with two button
	 *
	 * @param context
	 * @param title
	 * @param msg
	 * @param callback
	 * @return {@link Dialog}
	 */
	public static Dialog showDialog(Context context, String title, String msg, OnClickListener callback) {
		Builder builder = new Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setPositiveButton(android.R.string.ok, callback);
		builder.setNegativeButton(android.R.string.cancel, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		Dialog dialog = avoidDismiss(builder.create(), true);
		dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
		dialog.show();

		return dialog;
	}

	public static Dialog showDialog(Context context, String title, String msg, OnClickListener confirmAction, OnClickListener cancelAction) {
		Builder builder = new Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setPositiveButton(android.R.string.ok, confirmAction);
		if (cancelAction != null)
			builder.setNegativeButton(android.R.string.cancel, cancelAction);
		else {
			builder.setNegativeButton(android.R.string.cancel, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
		}
		Dialog dialog = avoidDismiss(builder.create(), true);
		dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
		dialog.show();
		return dialog;
	}

	/**
	 * 
	 * @param <T>
	 * @param t
	 * @return Dialog
	 */
	public static <T extends Dialog> T avoidDismiss(T t, boolean cancelable) {
		t.setCancelable(cancelable);
		t.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_SEARCH && event.getRepeatCount() == 0) {
					return true;
				}
				return false;
			}
		});
		return t;
	}
}