
import React from 'react'

export default function CostInfo ({total, currencySymbol}) {


    return (
        <div className="product-section">
            <div className="header">
            {/* <div className="nav-back">
                <button className="back-button">←</button>
                <div className="brand">
                <div className="brand-logo">P</div>
                <span className="brand-name">Powdur</span>
                <span className="test-mode">TEST MODE</span>
                </div> 
            </div> */}
            
            <div className="product-summary">
                <h2 className="pay-title">To Pay </h2>
                <h1 className="total-amount">{currencySymbol + total.toFixed(2)}</h1>
                
                {/* <div className="product-list">
                {products.map(product => (
                    <div key={product.id} className="product-item">
                    <img src={product.image} alt={product.name} className="product-thumbnail" />
                    <div className="product-details">
                        <div className="product-name">{product.name}</div>
                        <div className="product-quantity">Qty: {product.quantity}</div>
                    </div>
                    <div className="product-price">
                        ${product.price.toFixed(2)}
                        {product.quantity > 1 && (
                        <div className="price-per-item">${product.price.toFixed(2)} each</div>
                        )}
                    </div>
                    </div>
                ))}
                </div> */}
                
                {/* <div className="price-breakdown">
                <div className="price-row">
                    <span>Subtotal</span>
                    <span>${subtotal.toFixed(2)}</span>
                </div>
                <div className="price-row">
                    <div>
                    <span>Shipping</span>
                    <div className="shipping-detail">Ground shipping (3-5 business days)</div>
                    </div>
                    <span>${shipping.toFixed(2)}</span>
                </div>
                <div className="price-row total">
                    <span>Total due</span>
                    <span>${total.toFixed(2)}</span>
                </div>
                </div> */}
            </div> 
            
            
            </div>
      </div>
  )
}
