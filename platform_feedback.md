# Platform Feedback

Wrapping up the first iteration of Food Mart.
Here's what I'd want the mobile team to figure out early if this were a real product and we were about to build feature #2.

## Error handling

Right now `FoodListViewModel` catches a generic exception and sets an error string on the UI state.
That worked fine for one screen, but if we add order history or anything else that talks to the network, we'll probably copy-paste the same try/catch.
I'd introduce something like a shared `safeApiCall` wrapper so every screen handles failures the same way and we only have to test it once.

## Reusable UI pieces

Both screens ended up with similar patterns: loading spinner, error with retry, empty state on the cart, snackbar for purchase feedback.
I got these working but they're basically one-offs right now.
I'd pull them into shared composables before the next feature.
Same goes for the snackbar pattern in the cart (`userMessage` and clearing it after showing). That'll come up again.
The food list item card and cart item card are also almost identical (image, name, price, action button).
A shared row component with a slot for the trailing button would have saved me some duplication.

## Design system

I started with Material 3 defaults and a small `toPriceLabel()` helper.
That was enough for the take-home, but I ran into dynamic color breaking Compose previews, which made me wish colors and theme config lived in one place.
If a designer joined the team I'd want a proper design system module with spacing, typography, and shared components like `PriceText` and `ItemImage`.

## Testing

I'm happy with how testing turned out: repo tests, use case tests, ViewModel tests with MockK, and a `MainDispatcherRule` plus test fixtures for sample data.
But I set all of that up from scratch for this one app.

A shared `core:testing` module with the dispatcher rule, `foodItem()` builders, and a short note on when to mock vs test the real implementation would make the next feature faster to test.

## Fake APIs for unfinished backend work

The purchase endpoint isn't live, so I used `FakePurchaseApi` behind an interface and swapped it in through Hilt.
That let me build and demo the full purchase flow without waiting on backend.
I'd keep doing this: define the real Retrofit call, write a fake, bind whichever one the environment needs.

## Money

Prices are `Double` throughout the app.
Fine for showing `$1.49` on screen, but I'd be nervous comparing our cart total to the server's `total_price` or doing any real financial math with floats.
Eventually a `Money` type (cents stored as `Long`) would be cleaner.

## Project structure

I kept everything in one module with `data/`, `domain/`, and `ui/` packages and that felt right for this size.
If the app grew I'd split into `core:network`, `core:designsystem`, and `feature:*` modules.
The use cases are already natural cut points for that.

## Cart persistence

The cart lives in memory and dies on process death.
The assignment doesn't ask for persistence, so I didn't build it, but it's probably the first thing users would notice in a real app.
I'd pick DataStore as the default for cart state early so we don't end up with three different storage solutions across features.

## Stuff I'd also carry over from this project

Some of this overlaps with my README notes but worth repeating in a platform context.

- For git workflow I'd use feature branches tied to tickets, PRs, and code review.
- I committed linearly for the take-home but wouldn't do that on a team.
- For localization, strings are hardcoded in English right now. Lokalise or something similar would make sense for a real product.
- For security, if we were doing actual payments I'd defer to something like Stripe rather than rolling our own.
- For navigation routes, a small `Routes` object is fine for now.  I'd move it to a separate file and tighten the structure if the app got bigger.

Overall I enjoyed building this and Kotlin/Compose made a lot of the architecture feel natural.
The layers paid off when I added use cases and tests without touching the UI much.
