# Custom UI Mode Documentation

The DocumentActivity supports a flexible custom UI mode that allows you to customize the document viewer for your specific needs.

## Quick Example

```java
Intent intent = new Intent(this, DocumentActivity.class);
intent.setAction(Intent.ACTION_VIEW);
intent.setDataAndType(documentUri, mimeType);

// Enable custom UI
intent.putExtra(DocumentActivity.EXTRA_USE_CUSTOM_UI, true);
intent.putExtra(Intent.EXTRA_TITLE, "My Document.pdf");

// Show only search and share buttons
String[] buttons = {DocumentActivity.BUTTON_SEARCH, DocumentActivity.BUTTON_SHARE};
intent.putExtra(DocumentActivity.EXTRA_VISIBLE_BUTTONS, buttons);

// Enable voting (42 votes, user already voted)
intent.putExtra(DocumentActivity.EXTRA_SHOW_VOTE, true);
intent.putExtra(DocumentActivity.EXTRA_VOTE_COUNT, 42);
intent.putExtra(DocumentActivity.EXTRA_IS_VOTED, true);

startActivity(intent);
```

## Features Overview

## Features Overview

### 1. Custom Layout
- **Close Button ('X')**: Close the viewer without back button
- **Document Type Display**: Shows document type (PDF, EPUB, etc.) below title
- **Enhanced Page Number**: "Page 1 of 12" format
- **Vote of Relevance**: Optional voting section with thumbs up and count

### 2. Configurable Toolbar Buttons
Specify exactly which buttons appear in the toolbar.

### 3. Custom Overflow Menu (⋮)
Add custom menu items with your own actions or floating messages.

### 4. Advanced Navigation & Zoom Bar
- **Page Navigation**: `< | 1 / 12 | >` with highlighted current page
- **Smart Button States**: Arrows fade when at first/last page  
- **Zoom Controls**: `[-] 100% [+]` for easy zoom

### 5. Custom Callbacks
- Handle custom menu clicks
- Override share behavior
- Get notified of vote changes

---

## Vote of Relevance

Display a voting section where users can upvote documents.

### Setup

```java
intent.putExtra(DocumentActivity.EXTRA_SHOW_VOTE, true);
intent.putExtra(DocumentActivity.EXTRA_VOTE_COUNT, 42);      // Current vote count
intent.putExtra(DocumentActivity.EXTRA_IS_VOTED, true);      // User already voted
intent.putExtra(DocumentActivity.EXTRA_CALLBACK_KEY, "my_key");
```

### Handle Vote Changes

```java
DocumentCallbackRegistry.getInstance().registerCallback("my_key", 
    new DocumentActivityCallback() {
        @Override
        public void onVoteChanged(Context ctx, Uri uri, String title, 
                                 boolean isVoted, int newCount) {
            // isVoted: true if user just voted, false if removed vote
            // newCount: updated vote count
            // Sync with your backend here
        }
        
        // ... other callback methods
    });
```

The thumbs up button shows in blue when voted, faded when not voted.

---

## Custom Menu Items

### Regular Menu Items

```java
CustomMenuItem[] items = new CustomMenuItem[] {
    new CustomMenuItem("info", "Document Info"),
    new CustomMenuItem("qa", "Q&A"),
    new CustomMenuItem("settings", "Settings")
};
intent.putExtra(DocumentActivity.EXTRA_CUSTOM_MENU_ITEMS, items);
```

### Floating Message Menu Items

Create menu items that show temporary floating messages:

```java
CustomMenuItem floatingMsg = CustomMenuItem.createFloatingMessage(
    "note",                    // Menu item ID
    "Important Note",          // Menu title
    "This is confidential.",   // Message to display
    Typeface.BOLD_ITALIC       // Text style
);

CustomMenuItem[] items = new CustomMenuItem[] {
    new CustomMenuItem("info", "Info"),
    floatingMsg,
    new CustomMenuItem("settings", "Settings")
};
intent.putExtra(DocumentActivity.EXTRA_CUSTOM_MENU_ITEMS, items);
```

**Text Styles:**
- `Typeface.NORMAL`
- `Typeface.BOLD`  
- `Typeface.ITALIC`
- `Typeface.BOLD_ITALIC`

The floating message:
- Appears in top-right corner
- Max width 200dp, text wraps automatically
- Dismisses when user taps document or any button
- Can be dismissed by tapping the card itself

---

## Custom Share

Override the default share behavior:

```java
intent.putExtra(DocumentActivity.EXTRA_CUSTOM_SHARE, true);
intent.putExtra(DocumentActivity.EXTRA_CALLBACK_KEY, "my_key");

DocumentCallbackRegistry.getInstance().registerCallback("my_key",
    new DocumentActivityCallback() {
        @Override
        public boolean onShareDocument(Context ctx, Uri uri, String title) {
            // Share a link instead of the file
            String link = "https://myapp.com/doc/" + uri.getLastPathSegment();
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out: " + title + "\n" + link);
            ctx.startActivity(Intent.createChooser(shareIntent, "Share"));
            return true;  // We handled it
        }
        
        // ... other callback methods
    });
```

Return `true` to override default, `false` to use default share.

---
- **Close Button ('X')**: A prominent close button appears before the document title, allowing users to close the viewer without using the back button
- **Document Type Display**: Shows the document type (PDF, EPUB, DOC, etc.) below the title
- **Streamlined Design**: More compact, vertical-oriented toolbar design
- **Enhanced Page Number**: Displays "Page 1 of 12" format instead of "1/12"

### 2. Configurable Toolbar Buttons
Library users can specify exactly which toolbar buttons to display, making it easy to create a minimal, focused UI for specific use cases.

### 3. Custom Overflow Menu (⋮)
Add custom menu items to an overflow menu (3 vertical dots) that appears after your specified toolbar buttons. Perfect for app-specific actions like "Info", "Q&A", "Settings", etc.

### 4. Custom Action Callbacks
Implement custom behavior for:
- **Overflow menu item clicks**: Handle your custom menu actions
- **Share button override**: Implement custom sharing (e.g., share a link instead of the file)

### 5. Advanced Navigation & Zoom Bar
In custom UI mode, the bottom page slider is replaced with an enhanced navigation and zoom control bar:
- **Page Navigation**: `< | 1 / 12 | >` with current page number highlighted in blue (#1420FF)
- **Smart Button States**: Navigation arrows fade when at first/last page
- **Zoom Controls**: `[-] 100% [+]` buttons for easy zoom in/out
- **Real-time Percentage**: Shows current zoom level (e.g., 125%, 80%)

## Usage

### Basic Setup

To use the custom UI mode, add the following extras to your Intent when starting DocumentActivity:

```java
Intent intent = new Intent(this, DocumentActivity.class);
intent.setAction(Intent.ACTION_VIEW);
intent.setDataAndType(documentUri, mimeType);

// Enable custom UI mode
intent.putExtra(DocumentActivity.EXTRA_USE_CUSTOM_UI, true);

startActivity(intent);
```

### Optional: Custom Document Title

```java
// Provide a custom title for the document
intent.putExtra(Intent.EXTRA_TITLE, "My Custom Document Title");
```

### Optional: Document Type

```java
// Provide document type (will be auto-detected if not provided)
intent.putExtra(DocumentActivity.EXTRA_DOC_TYPE, "PDF");
```

Supported document types include: PDF, EPUB, MOBI, DOC, PPT, XLS, TXT, HTML, CBZ, FB2, XPS, ZIP, GZIP

### Optional: Visible Buttons Configuration

Specify which buttons should appear in the toolbar:

```java
// Show only search and share buttons
String[] visibleButtons = new String[] {
    DocumentActivity.BUTTON_SEARCH,
    DocumentActivity.BUTTON_SHARE
};
intent.putExtra(DocumentActivity.EXTRA_VISIBLE_BUTTONS, visibleButtons);
```

## Available Button Constants

| Constant | Description |
|----------|-------------|
| `BUTTON_COPY` | Copy text selection |
| `BUTTON_OUTLINE` | Table of contents / Bookmarks |
| `BUTTON_SINGLE_COLUMN` | Single column mode for dual-spread pages |
| `BUTTON_TEXT_LEFT` | Right-to-left text mode |
| `BUTTON_FLIP_VERTICAL` | Flip vertical mode |
| `BUTTON_LOCK` | Lock stray movement |
| `BUTTON_CROP_MARGIN` | Crop page margins |
| `BUTTON_FOCUS` | Focus mode (maintains position across pages) |
| `BUTTON_SMART_FOCUS` | Smart focus for scanned PDFs |
| `BUTTON_COLOR` | Color palette selector |
| `BUTTON_LAYOUT` | Font size adjustment (for reflowable documents) |
| `BUTTON_LINK` | Activate links |
| `BUTTON_SEARCH` | Text search |
| `BUTTON_SHARE` | Share document |
| `BUTTON_HELP` | Help documentation |

## Complete Example

```java
public class MyActivity extends Activity {
    private static final String CALLBACK_KEY = "my_callback";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Register callback for custom actions
        DocumentCallbackRegistry.getInstance().registerCallback(CALLBACK_KEY, 
            new DocumentActivityCallback() {
                @Override
                public void onCustomMenuItemClick(Context context, String menuItemId, 
                                                 Uri documentUri, String documentTitle) {
                    switch (menuItemId) {
                        case "info":
                            // Show document info
                            showDocumentInfo(documentTitle);
                            break;
                        case "qa":
                            // Open Q&A section
                            openQASection(documentUri);
                            break;
                    }
                }
                
                @Override
                public boolean onShareDocument(Context context, Uri documentUri, String documentTitle) {
                    // Custom share: Share a link instead of the file
                    String shareText = "Check out this material: " + documentTitle + 
                                     "\nhttps://example.com/docs/" + documentUri.getLastPathSegment();
                    
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                    context.startActivity(Intent.createChooser(shareIntent, "Share via"));
                    
                    return true; // We handled it
                }
            });
    }
    
    public void openDocumentWithCustomUI(Uri documentUri, String mimeType) {
        Intent intent = new Intent(this, DocumentActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(documentUri, mimeType);
        
        // Enable custom UI
        intent.putExtra(DocumentActivity.EXTRA_USE_CUSTOM_UI, true);
        
        // Set custom title
        intent.putExtra(Intent.EXTRA_TITLE, "Project Proposal.pdf");
        
        // Optionally set document type
        intent.putExtra(DocumentActivity.EXTRA_DOC_TYPE, "PDF");
        
        // Configure toolbar buttons
        String[] visibleButtons = new String[] {
            DocumentActivity.BUTTON_SEARCH,
            DocumentActivity.BUTTON_SHARE
        };
        intent.putExtra(DocumentActivity.EXTRA_VISIBLE_BUTTONS, visibleButtons);
        
        // Add custom overflow menu items
        CustomMenuItem[] customMenuItems = new CustomMenuItem[] {
            new CustomMenuItem("info", "Info"),
            new CustomMenuItem("qa", "Q&A"),
            new CustomMenuItem("settings", "Settings")
        };
        intent.putExtra(DocumentActivity.EXTRA_CUSTOM_MENU_ITEMS, customMenuItems);
        
        // Link callback for handling actions
        intent.putExtra(DocumentActivity.EXTRA_CALLBACK_KEY, CALLBACK_KEY);
        
        // Enable custom share handler
        intent.putExtra(DocumentActivity.EXTRA_CUSTOM_SHARE, true);
        
        startActivity(intent);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up callback
        DocumentCallbackRegistry.getInstance().unregisterCallback(CALLBACK_KEY);
    }
}
```

## Custom Overflow Menu

### Adding Custom Menu Items

Create menu items with unique IDs and titles:

```java
CustomMenuItem[] customMenuItems = new CustomMenuItem[] {
    new CustomMenuItem("info", "Document Info"),
    new CustomMenuItem("qa", "Q&A"),
    new CustomMenuItem("bookmark_sync", "Sync Bookmarks"),
    new CustomMenuItem("settings", "Settings")
};
intent.putExtra(DocumentActivity.EXTRA_CUSTOM_MENU_ITEMS, customMenuItems);
```

### Handling Menu Item Clicks

Implement the `DocumentActivityCallback` interface:

```java
DocumentCallbackRegistry.getInstance().registerCallback(CALLBACK_KEY, 
    new DocumentActivityCallback() {
        @Override
        public void onCustomMenuItemClick(Context context, String menuItemId, 
                                         Uri documentUri, String documentTitle) {
            // Handle based on menu item ID
            if (menuItemId.equals("info")) {
                // Show info dialog
            } else if (menuItemId.equals("qa")) {
                // Open Q&A activity
            }
        }
        
        @Override
        public boolean onShareDocument(Context context, Uri documentUri, String documentTitle) {
            // Not overriding share, use default
            return false;
        }
    });
```

## Custom Share Behavior

### Override Default Share

To implement custom sharing (e.g., share a link instead of the file):

```java
// Enable custom share
intent.putExtra(DocumentActivity.EXTRA_CUSTOM_SHARE, true);
intent.putExtra(DocumentActivity.EXTRA_CALLBACK_KEY, CALLBACK_KEY);

// Implement callback
DocumentCallbackRegistry.getInstance().registerCallback(CALLBACK_KEY, 
    new DocumentActivityCallback() {
        @Override
        public void onCustomMenuItemClick(Context context, String menuItemId, 
                                         Uri documentUri, String documentTitle) {
            // Handle menu clicks
        }
        
        @Override
        public boolean onShareDocument(Context context, Uri documentUri, String documentTitle) {
            // Custom share implementation
            String shareText = "Check out: " + documentTitle + "\nhttps://myapp.com/docs/123";
            
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, documentTitle);
            
            context.startActivity(Intent.createChooser(shareIntent, "Share"));
            
            return true; // Return true to indicate custom handling
        }
    });
```

Return `true` from `onShareDocument()` to prevent the default share behavior. Return `false` to allow default sharing.

## Callback Registry

The `DocumentCallbackRegistry` is a singleton that holds callback references. This is necessary because callback interfaces cannot be passed via Intent extras.

**Important**: Always unregister callbacks in your activity's `onDestroy()` to prevent memory leaks:

```java
@Override
protected void onDestroy() {
    super.onDestroy();
    DocumentCallbackRegistry.getInstance().unregisterCallback(CALLBACK_KEY);
}
```

## Intent Extras Reference

| Extra | Type | Description |
|-------|------|-------------|
| `EXTRA_USE_CUSTOM_UI` | boolean | Enable custom UI layout |
| `EXTRA_TITLE` | String | Custom document title |
| `EXTRA_DOC_TYPE` | String | Document type label (auto-detected if not provided) |
| `EXTRA_VISIBLE_BUTTONS` | String[] | Array of button identifiers to show |
| `EXTRA_CUSTOM_MENU_ITEMS` | CustomMenuItem[] | Overflow menu items |
| `EXTRA_CALLBACK_KEY` | String | Key to retrieve callback from registry |
| `EXTRA_CUSTOM_SHARE` | boolean | Enable custom share handler |
| `EXTRA_SHOW_VOTE` | boolean | Show vote of relevance section |
| `EXTRA_VOTE_COUNT` | int | Initial vote count |
| `EXTRA_IS_VOTED` | boolean | User's current vote status |

### Button Identifiers

```java
BUTTON_COPY            // Copy text selection
BUTTON_OUTLINE         // Table of contents
BUTTON_SINGLE_COLUMN   // Single column mode
BUTTON_TEXT_LEFT       // Right-to-left text
BUTTON_FLIP_VERTICAL   // Flip vertical
BUTTON_LOCK            // Lock stray
BUTTON_CROP_MARGIN     // Crop margins
BUTTON_FOCUS           // Focus mode
BUTTON_SMART_FOCUS     // Smart focus
BUTTON_COLOR           // Color palette
BUTTON_LAYOUT          // Layout options
BUTTON_LINK            // Activate links
BUTTON_SEARCH          // Search
BUTTON_SHARE           // Share document
BUTTON_HELP            // Help
```

---

## Complete Example

```java
public class MyActivity extends Activity {
    private static final String CALLBACK_KEY = "doc_viewer";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Register callback
        DocumentCallbackRegistry.getInstance().registerCallback(CALLBACK_KEY,
            new DocumentActivityCallback() {
                @Override
                public void onCustomMenuItemClick(Context ctx, String id, 
                                                 Uri uri, String title) {
                    if ("info".equals(id)) {
                        showDocumentInfo(title);
                    }
                }
                
                @Override
                public boolean onShareDocument(Context ctx, Uri uri, String title) {
                    // Custom share implementation
                    shareAsLink(uri, title);
                    return true;
                }
                
                @Override
                public void onVoteChanged(Context ctx, Uri uri, String title,
                                         boolean isVoted, int newCount) {
                    // Sync vote with backend
                    syncVoteToServer(uri, isVoted, newCount);
                }
            });
    }
    
    private void openDocument(Uri uri, String mimeType) {
        Intent intent = new Intent(this, DocumentActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, mimeType);
        
        // Enable custom UI
        intent.putExtra(DocumentActivity.EXTRA_USE_CUSTOM_UI, true);
        intent.putExtra(Intent.EXTRA_TITLE, "Annual Report 2024.pdf");
        
        // Configure buttons
        String[] buttons = {
            DocumentActivity.BUTTON_SEARCH,
            DocumentActivity.BUTTON_SHARE
        };
        intent.putExtra(DocumentActivity.EXTRA_VISIBLE_BUTTONS, buttons);
        
        // Add menu items
        CustomMenuItem[] menu = {
            new CustomMenuItem("info", "Info"),
            CustomMenuItem.createFloatingMessage("note", "Note",
                "Confidential - Internal Use Only", Typeface.BOLD),
            new CustomMenuItem("download", "Download")
        };
        intent.putExtra(DocumentActivity.EXTRA_CUSTOM_MENU_ITEMS, menu);
        
        // Enable voting (user already voted, 128 total votes)
        intent.putExtra(DocumentActivity.EXTRA_SHOW_VOTE, true);
        intent.putExtra(DocumentActivity.EXTRA_VOTE_COUNT, 128);
        intent.putExtra(DocumentActivity.EXTRA_IS_VOTED, true);
        
        // Link callback
        intent.putExtra(DocumentActivity.EXTRA_CALLBACK_KEY, CALLBACK_KEY);
        intent.putExtra(DocumentActivity.EXTRA_CUSTOM_SHARE, true);
        
        startActivity(intent);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        DocumentCallbackRegistry.getInstance().unregisterCallback(CALLBACK_KEY);
    }
}
```

---

## Best Practices

1. **Always unregister callbacks** in `onDestroy()` to prevent memory leaks
2. **Use unique callback keys** for each activity or use case
3. **Check vote state on open** - pass `EXTRA_IS_VOTED: true` if user already voted
4. **Sync vote changes** to your backend in the `onVoteChanged()` callback
5. **Keep floating messages concise** - they're meant for brief notifications
6. **Test both voted/unvoted states** to ensure UI feedback is clear

---

## Troubleshooting

**Vote button not showing?**
- Make sure `EXTRA_SHOW_VOTE` is set to `true`
- Verify you're using custom UI mode (`EXTRA_USE_CUSTOM_UI: true`)

**Floating message not dismissing?**
- The message auto-dismisses on any document or button tap
- You can also tap the floating card itself to dismiss it

**Callback not called?**
- Verify callback is registered **before** starting DocumentActivity
- Check that `EXTRA_CALLBACK_KEY` matches your registration key
- Ensure callback registry key is consistent

**Vote not showing as voted initially?**
- Set `EXTRA_IS_VOTED: true` when user has already voted
- The thumbs up will show in blue (#1420FF) when voted

---

## Migration from Default UI

Existing code continues to work without changes. To adopt custom UI:

1. Add `EXTRA_USE_CUSTOM_UI: true`
2. Optionally specify buttons, title, vote section
3. Register callbacks if using custom menu/share/vote

The custom UI is completely opt-in and backward compatible.

## Classes

### CustomMenuItem

Represents a custom menu item for the overflow menu.

```java
// Constructor with just ID and title
CustomMenuItem item = new CustomMenuItem("info", "Document Info");

// Constructor with icon (optional)
CustomMenuItem item = new CustomMenuItem("info", "Document Info", R.drawable.ic_info);
```

**Methods:**
- `String getId()` - Returns unique identifier
- `String getTitle()` - Returns display title
- `int getIconResId()` - Returns icon resource ID (0 if none)

### DocumentActivityCallback

Interface for handling custom actions.

```java
public interface DocumentActivityCallback {
    void onCustomMenuItemClick(Context context, String menuItemId, 
                              Uri documentUri, String documentTitle);
    
    boolean onShareDocument(Context context, Uri documentUri, String documentTitle);
}
```

### DocumentCallbackRegistry

Singleton registry for managing callbacks.

```java
// Register
DocumentCallbackRegistry.getInstance().registerCallback(key, callback);

// Retrieve
DocumentActivityCallback callback = DocumentCallbackRegistry.getInstance().getCallback(key);

// Unregister
DocumentCallbackRegistry.getInstance().unregisterCallback(key);

// Clear all
DocumentCallbackRegistry.getInstance().clear();
```

If you don't provide `EXTRA_DOC_TYPE`, the library will automatically detect it based on:
1. MIME type
2. File extension (fallback)

The detected type will be displayed in the UI.

## Default Behavior

- If `EXTRA_USE_CUSTOM_UI` is not set or is `false`, the library uses the default layout
- If `EXTRA_VISIBLE_BUTTONS` is not provided in custom UI mode, all buttons will be hidden by default in the custom layout
- If `EXTRA_DOC_TYPE` is not provided, it will be auto-detected
- If `EXTRA_CUSTOM_MENU_ITEMS` is not provided or empty, the overflow button won't appear
- If `EXTRA_CUSTOM_SHARE` is not set or `false`, default share behavior is used
- If callback returns `false` from `onShareDocument()`, default share behavior is used

## Example Implementation

See `CustomUIActivity.java` in the app module for a complete working example demonstrating:
- Custom UI layout
- Configurable toolbar buttons
- Custom overflow menu items
- Custom menu item click handling  
- Custom share implementation

## Layout Differences

### Default Layout
- Horizontal scrollable toolbar
- All buttons visible by default
- Document title only
- No overflow menu
- Standard share behavior

### Custom Layout
- Close button ('X') before title
- Document title + type in vertical stack
- Enhanced page number format: "Page 1 of 12"
- Horizontal scrollable buttons area
- Only specified buttons visible
- Optional overflow menu (⋮) with custom items
- Optional custom share behavior
- **Advanced navigation bar**: Page navigation (`< 1/12 >`) with smart button fading
- **Integrated zoom controls**: Zoom in/out buttons with percentage display
- More compact design

## Migration Guide

Existing code will continue to work without changes. To adopt the custom UI:

1. Add `EXTRA_USE_CUSTOM_UI` flag
2. Optionally specify visible buttons
3. Optionally provide custom title and document type
4. Optionally add custom overflow menu items
5. Optionally implement callbacks for custom actions

The custom UI mode is completely opt-in and backward compatible.

## Best Practices

1. **Always unregister callbacks** in `onDestroy()` to prevent memory leaks
2. **Use unique callback keys** for each activity/use case
3. **Keep menu item IDs simple and descriptive** (e.g., "info", "settings", "qa")
4. **Provide user feedback** in menu item handlers (Toast, Dialog, Navigation, etc.)
5. **Test both custom and default share** to ensure proper fallback behavior
6. **Consider null checks** in callback implementations for robustness

## Troubleshooting

**Menu items not appearing?**
- Ensure `EXTRA_CUSTOM_MENU_ITEMS` array is not null or empty
- Check that callback is registered before starting DocumentActivity

**Share button not using custom handler?**
- Make sure `EXTRA_CUSTOM_SHARE` is set to `true`
- Verify `EXTRA_CALLBACK_KEY` is provided
- Ensure callback is registered with the same key
- Return `true` from `onShareDocument()` to override default

**Memory leaks?**
- Always call `unregisterCallback()` in `onDestroy()`
- Consider using WeakReferences in callbacks if needed

**Callback not called?**
- Verify the callback key matches between registration and intent extra
- Check that DocumentCallbackRegistry.getInstance() returns the same instance
