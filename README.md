# Food Mart

Take home assignment for the 7shifts mobile team.
A small grocery shopping app where you browse food items, sort and filter them, add stuff to a cart, and purchase everything at the end.

## What it does

**Food list screen**

Browse all food items from the API.
Each row shows the name, price, category, and image.
Sort by price (low to high or high to low).
Filter by one or more categories.
Add items to the cart (same item can be added multiple times).
Cart icon shows a badge with the total number of items.

**Cart screen**

See everything in your cart with quantities and line totals.
Remove items.
Tap Purchase to submit the order.
Cart clears after a successful purchase.

## Deliverables

`issue_breakdown.md` breaks the requirements into sprint sized tasks with assumptions.

`purchase_api_contract.md` proposes the request/response format for the purchase endpoint (the backend one isn't implemented yet).

`platform_feedback.md` covers what I'd standardize or abstract for the next iteration of the app.

## Tech stack

Kotlin + Jetpack Compose (Material 3)
MVVM with data / domain / ui layers
Hilt for dependency injection
Retrofit + kotlinx.serialization for networking
Coil for images
MockK + Turbine for unit tests
minSdk 26, compileSdk 34

## Project structure

Everything lives under `app/src/main/java/com/example/foodmart/`:

* `data/` — DTOs, Retrofit API, mappers, repository implementations
* `domain/` — models, repository interfaces, use cases
* `ui/` — Compose screens, ViewModels, navigation, theme
* `di/` — Hilt modules

The purchase flow uses a `FakePurchaseApi` behind an interface since the real endpoint doesn't exist yet.
Swapping to the real Retrofit implementation is a one line change in `di/DataModule.kt`.

## Running the app

Open the project in Android Studio and hit Run.

Or from the terminal:

```bash
./gradlew installDebug
adb shell am start -n com.example.foodmart/.MainActivity
```

You'll need an emulator or physical device connected, plus JDK 17 and the Android SDK.

## Running tests

```bash
./gradlew testDebugUnitTest
```

Covers DTO parsing, repository mapping, cart behaviour, use cases, and ViewModel logic (sorting, filtering, purchase success/failure).

## API endpoints used

```
GET https://7shifts.github.io/mobile-takehome/api/food_items.json
GET https://7shifts.github.io/mobile-takehome/api/food_item_categories.json
```

Purchase endpoint is proposed in `purchase_api_contract.md`.

## Notes

This was a fun exercise and I enjoyed the process of implementing this little app.
Kotlin is such an elegant language with intuitive features. I missed doing this.

### Things I would do differently in an _actual_ product setting

For git workflow I'd use branches that track a matching ticket number.
That would allow for PRs and a review process, which is vital when working on a team.
I committed mostly linearly for the take home but wouldn't do that in production.

For strings I'd use something like Lokalise instead of hardcoding English text everywhere.
Since this is a take home assignment I kept copy simple, but localization would matter for a real product.

I'd give the project a proper package name.
Right now it's `com.example.foodmart` because I started from a basic Android Studio template.

If the app grew I'd move navigation routes to a stricter setup in its own file.
Fine for two screens, but a larger app needs clearer separation.

For payments I'd think about security and data encryption and probably defer to a provider like Stripe rather than rolling our own checkout.

### Exploration before coding

I played around with the endpoints in Postman first to understand the JSON shape and how categories link to items.
I also practiced beforehand with similarly sized apps so I had a feel for the architecture before diving in.
