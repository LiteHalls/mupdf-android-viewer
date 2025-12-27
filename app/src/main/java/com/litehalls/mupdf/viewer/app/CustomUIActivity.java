package com.litehalls.mupdf.viewer.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.artifex.mupdf.fitz.Document;
import com.litehalls.mupdf.viewer.CustomMenuItem;
import com.litehalls.mupdf.viewer.DocumentActivity;
import com.litehalls.mupdf.viewer.DocumentActivityCallback;
import com.litehalls.mupdf.viewer.DocumentCallbackRegistry;

/**
 * Example activity showing how to use the custom UI mode for DocumentActivity.
 * This demonstrates:
 * 1. Using custom UI layout with close button and document type
 * 2. Providing custom title
 * 3. Specifying which buttons to show in the toolbar
 * 4. Adding custom overflow menu items
 * 5. Handling custom menu item clicks
 * 6. Overriding share button behavior
 */
public class CustomUIActivity extends Activity
{
	protected final int FILE_REQUEST = 42;
	protected boolean selectingDocument;
	private static final String CALLBACK_KEY = "custom_ui_callback";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		selectingDocument = false;
		
		// Register callback for handling custom actions
		DocumentCallbackRegistry.getInstance().registerCallback(CALLBACK_KEY, new DocumentActivityCallback() {
			@Override
			public void onCustomMenuItemClick(Context context, String menuItemId, Uri documentUri, String documentTitle) {
				// Handle custom menu item clicks
				switch (menuItemId) {
					case "info":
						Toast.makeText(context, "Info clicked for: " + documentTitle, Toast.LENGTH_SHORT).show();
						// You could open an info dialog, navigate to another activity, etc.
						break;
					case "qa":
						Toast.makeText(context, "Q&A clicked for: " + documentTitle, Toast.LENGTH_SHORT).show();
						// You could open Q&A section, show FAQ, etc.
						break;
					case "settings":
						Toast.makeText(context, "Settings clicked", Toast.LENGTH_SHORT).show();
						// Open settings, preferences, etc.
						break;
					default:
						Toast.makeText(context, "Unknown menu item: " + menuItemId, Toast.LENGTH_SHORT).show();
						break;
				}
			}
			
			@Override
			public boolean onShareDocument(Context context, Uri documentUri, String documentTitle) {
				// Custom share implementation
				// For example, share a link instead of the document file
				String shareText = "Check out this material: " + documentTitle + "\nhttps://example.com/docs/" + documentUri.getLastPathSegment();
				
				Intent shareIntent = new Intent(Intent.ACTION_SEND);
				shareIntent.setType("text/plain");
				shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
				shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Shared Document: " + documentTitle);
				
				context.startActivity(Intent.createChooser(shareIntent, "Share via"));
				
				// Return true to indicate we handled the share
				return true;
			}
			
			@Override
			public void onVoteChanged(Context context, Uri documentUri, String documentTitle, boolean isVoted, int newVoteCount) {
				// Handle vote changes
				String message = isVoted ? "Voted! Total: " + newVoteCount : "Vote removed. Total: " + newVoteCount;
				Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
				// You could sync with your backend here
			}
		});
	}

	public void onStart() {
		super.onStart();
		if (!selectingDocument)
		{
			Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
			intent.addCategory(Intent.CATEGORY_OPENABLE);
			intent.setType("*/*");
			intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {
				"application/pdf",
				"application/vnd.ms-xpsdocument",
				"application/oxps",
				"application/x-cbz",
				"application/vnd.comicbook+zip",
				"application/epub+zip",
				"application/x-fictionbook",
				"application/x-mobipocket-ebook",
				"application/vnd.openxmlformats-officedocument.wordprocessingml.document",
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
				"application/vnd.openxmlformats-officedocument.presentationml.presentation",
				"text/html",
				"text/plain",
				"application/x-gzip",
				"application/zip",
				"application/octet-stream"
			});

			startActivityForResult(intent, FILE_REQUEST);
			selectingDocument = true;
		}
	}

	public void onActivityResult(int request, int result, Intent data) {
		if (request == FILE_REQUEST && result == Activity.RESULT_OK) {
			if (data != null) {
				Intent intent = new Intent(this, DocumentActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
				intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
				intent.setAction(Intent.ACTION_VIEW);
				intent.setDataAndType(data.getData(), data.getType());
				
				// Enable custom UI mode
				intent.putExtra(DocumentActivity.EXTRA_USE_CUSTOM_UI, true);
				
				// Optional: Provide custom title
				intent.putExtra(Intent.EXTRA_TITLE, "My Custom Document");
				
				// Optional: Provide document type (if not provided, it will be auto-detected)
				// intent.putExtra(DocumentActivity.EXTRA_DOC_TYPE, "PDF");
				
				// Optional: Specify which buttons to show (only search and share in this example)
				String[] visibleButtons = new String[] {
					DocumentActivity.BUTTON_SEARCH,
					DocumentActivity.BUTTON_SHARE
				};
				intent.putExtra(DocumentActivity.EXTRA_VISIBLE_BUTTONS, visibleButtons);
				
				// NEW: Add custom overflow menu items
				CustomMenuItem[] customMenuItems = new CustomMenuItem[] {
					new CustomMenuItem("info", "Info"),
					new CustomMenuItem("qa", "Q&A"),
					CustomMenuItem.createFloatingMessage("note", "Important Note", 
						"This document contains confidential information. Please do not share.", 
						android.graphics.Typeface.BOLD_ITALIC),
					new CustomMenuItem("settings", "Settings")
				};
				intent.putExtra(DocumentActivity.EXTRA_CUSTOM_MENU_ITEMS, customMenuItems);
				
				// NEW: Register callback key for handling menu clicks and custom share
				intent.putExtra(DocumentActivity.EXTRA_CALLBACK_KEY, CALLBACK_KEY);
				
				// NEW: Enable custom share handler (override default share behavior)
				intent.putExtra(DocumentActivity.EXTRA_CUSTOM_SHARE, true);
				
				// NEW: Enable vote section
				intent.putExtra(DocumentActivity.EXTRA_SHOW_VOTE, true);
				intent.putExtra(DocumentActivity.EXTRA_VOTE_COUNT, 42); // Current vote count
				intent.putExtra(DocumentActivity.EXTRA_IS_VOTED, false); // User hasn't voted yet
				
				// You can also show more buttons as needed:
				// String[] visibleButtons = new String[] {
				//     DocumentActivity.BUTTON_SEARCH,
				//     DocumentActivity.BUTTON_SHARE,
				//     DocumentActivity.BUTTON_OUTLINE,
				//     DocumentActivity.BUTTON_COLOR,
				//     DocumentActivity.BUTTON_FOCUS
				// };
				
				intent.putExtra(getComponentName().getPackageName() + ".ReturnToLibraryActivity", 1);
				startActivity(intent);
			}
			if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.S_V2)
				finish();
		} else if (request == FILE_REQUEST && result == Activity.RESULT_CANCELED) {
			finish();
		}
		selectingDocument = false;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// Clean up callback when activity is destroyed
		DocumentCallbackRegistry.getInstance().unregisterCallback(CALLBACK_KEY);
	}
}
