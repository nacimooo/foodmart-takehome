# Purchase API Contract

Since the purchase endpoint isn't built yet, here's what I'd want the backend to implement for the cart purchase flow.
I'm going to try and be as explicit as possible so that no context or ambiguity is left.

Since also this isn't a thing I'll stub this out for functionality

## Endpoint

```
POST /api/purchase
Content-Type: application/json
```

Base URL would be the same as the other endpoints:
`https://7shifts.github.io/mobile-takehome/`

## Request

Send the items in the cart. Just uuid and quantity so the server should look up prices, not trust whatever the app sends.

```json
{
  "items": [
    {
      "food_item_uuid": "xxxx",
      "quantity": 2
    },
    {
      "food_item_uuid": "xxx",
      "quantity": 1
    }
  ]
}
```

- `items` : required, can't be empty
- `food_item_uuid` : matches the uuid from the food items endpoint
- `quantity` : must be at least 1

## Success response

`201 Created`

```json
{
  "order_uuid": "xxx",
  "status": "completed",
  "total_price": 18.67,
  "created_at": "2026-07-27T18:30:00Z"
}
```

- `order_uuid` : so we could show order history later
- `status` : "completed" for now
- `total_price` : calculated on the server side
- `created_at` : timestamp

## Errors

Something simple and consistent:

```json
{
  "error": {
    "code": "invalid_item",
    "message": "Food item does not exist."
  }
}
```

## Error Status Messages

- 400 Bad Request: Empty items, bad quantity, etc.
- 422 Unprocessable Entity: Item UUID not found
- 500 Internal Server Error: Server error

## What the app does with this

- 201: clear cart, show "order placed" snackbar
- Anything else: keep cart as-is so user can retry