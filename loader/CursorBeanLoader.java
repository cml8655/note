package com.example.tt;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import android.annotation.TargetApi;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class CursorBeanLoader<T> extends AsyncTaskLoader<List<T>> {

	final ForceLoadContentObserver mObserver;

	private Uri mUri;
	private String[] mProjection;
	private String mSelection;
	private String[] mSelectionArgs;
	private String mSortOrder;

	private List<T> mData;

	public CursorBeanLoader(Context context) {
		super(context);
		mObserver = new ForceLoadContentObserver();
	}

	@Override
	public List<T> loadInBackground() {
		Cursor cursor = getContext().getContentResolver().query(mUri,
				mProjection, mSelection, mSelectionArgs, mSortOrder);
		if (cursor != null) {
			// TODO beanwrap
			cursor.close();
		}

		return null;
	}

	/* Runs on the UI thread */
	@Override
	public void deliverResult(List<T> data) {
		if (isReset()) {
			// An async query came in while the loader is stopped
			return;
		}
		mData = data;

		if (isStarted()) {
			super.deliverResult(data);
		}
	}

	/**
	 * Starts an asynchronous load of the contacts list data. When the result is
	 * ready the callbacks will be called on the UI thread. If a previous load
	 * has been completed and is still valid the result may be passed to the
	 * callbacks immediately.
	 *
	 * Must be called from the UI thread
	 */
	@Override
	protected void onStartLoading() {
		if (mData != null) {
			deliverResult(mData);
		}
		if (takeContentChanged() || mData == null) {
			forceLoad();
		}
	}

	/**
	 * Must be called from the UI thread
	 */
	@Override
	protected void onStopLoading() {
		// Attempt to cancel the current load task if possible.
		cancelLoad();
	}

	@Override
	public void onCanceled(List<T> data) {
	}

	@Override
	protected void onReset() {
		super.onReset();

		// Ensure the loader is stopped
		onStopLoading();
	}

	public Uri getUri() {
		return mUri;
	}

	public void setUri(Uri uri) {
		mUri = uri;
	}

	public String[] getProjection() {
		return mProjection;
	}

	public void setProjection(String[] projection) {
		mProjection = projection;
	}

	public String getSelection() {
		return mSelection;
	}

	public void setSelection(String selection) {
		mSelection = selection;
	}

	public String[] getSelectionArgs() {
		return mSelectionArgs;
	}

	public void setSelectionArgs(String[] selectionArgs) {
		mSelectionArgs = selectionArgs;
	}

	public String getSortOrder() {
		return mSortOrder;
	}

	public void setSortOrder(String sortOrder) {
		mSortOrder = sortOrder;
	}

	@Override
	public void dump(String prefix, FileDescriptor fd, PrintWriter writer,
			String[] args) {
		super.dump(prefix, fd, writer, args);
		writer.print(prefix);
		writer.print("mUri=");
		writer.println(mUri);
		writer.print(prefix);
		writer.print("mProjection=");
		writer.println(Arrays.toString(mProjection));
		writer.print(prefix);
		writer.print("mSelection=");
		writer.println(mSelection);
		writer.print(prefix);
		writer.print("mSelectionArgs=");
		writer.println(Arrays.toString(mSelectionArgs));
		writer.print(prefix);
		writer.print("mSortOrder=");
		writer.println(mSortOrder);
		writer.print(prefix);
		writer.print("mCursor=");
		writer.println(mData);
		writer.print(prefix);
	}

}
