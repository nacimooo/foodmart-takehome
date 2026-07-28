# Issue Breakdown

How I'd break this up if we were doing it in a sprint.
Each ticket has what I'd build and any assumptions I made along the way.

## 1. Project setup

New Android project with Kotlin + Compose.
Set up the basic folder structure: data / domain / ui.
Add Hilt, Retrofit, Coil.
MVVM for the screens.

Assumptions:
minSdk 26 is fine for this.
One module is enough for now, not splitting into feature modules yet.

## 2. Load and show food items

Call the food items and categories endpoints.
Map the JSON to domain models (join category name onto each item using category_uuid).
Show a list with image, name, price, and category.
Loading spinner and an error message with retry.

Assumptions:
If a category uuid doesn't match anything, show "Uncategorized".
No pagination needed, the list is only about 30 items.
Prices in dollars, two decimal places.

## 3. Sort by price

Add two chips for price low to high and high to low.
Sorting applies to whatever is currently on screen, including after filtering.

Assumptions:
Default order is whatever the API returns.
Tapping the same sort again turns it off.

## 4. Filter by category

Fetch categories and show a chip for each one.
User can pick multiple categories (OR filter, item shows if it matches any selected chip).
Filtering works together with sorting.

Assumptions:
No chips selected means show everything.
An empty result is fine if you filter to a category with no items.

## 5. Cart and badge

In memory cart as a singleton so both screens see the same state.
Tap + on an item to add it.
Same item added twice bumps quantity instead of adding a second row.
Cart icon in the top bar with a badge showing total item count.

Assumptions:
Cart doesn't need to survive app restart for v1.
"Total items" means sum of quantities (2 bananas + 1 apple = 3 on the badge).

## 6. Cart screen

Second screen showing cart items with quantity and line total.
Remove button per item (removes the whole line).
Empty state when the cart is empty.
Cart total at the bottom.

Assumptions:
Remove deletes that item entirely, not decrement by one. Could add that later.
Cart still doesn't need to survive restart for v1.

## 7. Purchase

Purchase button sends everything in the cart.
Write up the API contract (see purchase_api_contract.md) since the backend isn't ready.
Fake the API call so the flow actually works in the app.
On success: clear cart and show a message. On failure: keep cart and show an error.

Assumptions:
Backend endpoint doesn't exist yet so I'm using a fake implementation behind an interface.
Only clear the cart if the purchase succeeds.

## 8. Use cases and wiring

Add use cases between ViewModels and repositories (get items, add to cart, purchase cart, etc.).
Wire ViewModels through use cases instead of calling repositories directly.
Hilt modules for networking and repository bindings.

Assumptions:
Some use cases are thin one liners but they keep ViewModels testable and give business rules a home (like clearing the cart only after purchase).

## 9. Tests

ViewModel tests for sorting, filtering, cart count, purchase success and failure.
Repository tests for DTO mapping and category join.
Use case test for purchase clearing the cart only on success.
Use MockK for mocking.

Assumptions:
Unit tests only, not going for 100% coverage.
Focus on logic that's easy to break.
