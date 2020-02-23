# ShoppingCart

Can be tested out with a swagger at https://lego-shopping-cart.herokuapp.com/

# Environment Variables
To be added if run locally.
```
DATABASE_URL=postgres://svzozmnrxzwsts:fbf8bf561c0b30bf4f4ae81e58cc17e3bdaf032cfe7218327322e16556562662@ec2-54-246-90-10.eu-west-1.compute.amazonaws.com:5432/debjh3deh3r45b
```

# Authentication
To authenticate use the following credentials:
```
U: lego
PW: hireme
```

# Testing
To see how the API correctly gets products and returns them when calling the get basket endpoint use the following UUID

```
496fe520-5631-11ea-82b4-0242ac130003
```
It should return a list like this
```
[
    {
      "name": "Lego Man",
      "description": "A lego man to play with",
      "price": 100,
      "amount": 5
    },
    {
      "name": "Lego House",
      "description": "Lego house to play with your lego man in",
      "price": 500,
      "amount": 1
    },
    {
      "name": "Lego Millenium Falcon",
      "description": "It's the falcon!",
      "price": 10000,
      "amount": 5
    }
  ]
```