# Issue Breakdown

How I'd break this up if we were doing it in a sprint. Each ticket has what I'd build and any assumptions I made along the way.

---

## 1. Project setup

- New Android project with Kotlin + Compose
- Set up basic folder structure: data / domain / ui
- Add Hilt, Retrofit, Coil
- MVVM for the screens

**Assumptions**
- minSdk 26 is fine for this
- One module is enough, not splitting into feature modules yet

---

## 2. Load and show food items

- Call the food items + categories endpoints
- Map the JSON to domain models (join category name onto each item using category_uuid)
- Show a list with image, name, price, category
- Loading spinner + error message with retry

**Assumptions**
- If a category uuid doesn't match anything, just show "Uncategorized"
- No pagination needed, list is only ~30 items
- Prices in dollars, 2 decimal places

---

## 3. Sort by price

- Add two buttons/chips: price low→high and high→low
- Sort applies to whatever's currently showing (including after filtering)

**Assumptions**
- Default order = whatever the API returns
- Tap the same sort again to turn it off

---

## 4. Filter by category

- Fetch categories, show a chip for each one
- User can pick multiple categories (OR filter — item shows if it matches any selected chip)
- Works together with sorting

**Assumptions**
- No chips selected = show everything
- Empty result is ok (e.g. filter to a category with no items)

---

## 5. Cart + badge

- In-memory cart (singleton so both screens see the same cart)
- Tap + on an item to add it
- Same item added twice = quantity goes up, not a second row
- Cart icon in top bar with badge showing total item count

**Assumptions**
- Cart doesn't need to survive app restart for v1
- "Total items" = sum of quantities (2 bananas + 1 apple = 3 on the badge)

---

## 6. Cart screen

- Second screen showing cart items with qty and line total
- Remove button per item (removes the whole line)
- Empty state when nothing in cart
- Show cart total at bottom

**Assumptions**
- Remove = delete that item entirely, not decrement by 1 (could add that later)
- cart doesn't need to survive restart for v1"

---

## 7. Purchase

- Purchase button sends everything in the cart
- Write up the API contract (see purchase_api_contract.md) since backend isn't ready
- Fake the API call for now so the flow actually works
- On success: clear cart + show a message. On fail: keep cart + show error

**Assumptions**
- Backend endpoint doesn't exist yet so I'm using a fake implementation
- Only clear the cart if purchase succeeds

---

## 8. Tests

- ViewModel tests: sorting, filtering, cart count, purchase success/fail
- Repository test: DTO mapping + category join
- Use MockK for mocking

**Assumptions**
- Unit tests only, not going for 100% coverage
- Focus on the logic that's easy to break
