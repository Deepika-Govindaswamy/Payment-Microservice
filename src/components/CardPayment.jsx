import React, { useState, useCallback } from 'react';
import { CardElement, useStripe, useElements } from '@stripe/react-stripe-js';
import CardPaymentUI from './CardPaymentUI';


export default function CardPayment({ total, stripePromise, currency, setCurrency, currencySymbol }) {
  
    const stripe = useStripe();
    const elements = useElements();
    

    const [email, setEmail] = useState('');
    const [billingName, setBillingName] = useState('');
    const [city, setCity] = useState('');
    
    const [isProcessing, setIsProcessing] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');


  

  const handlePaymentRequest = async () => {
    if (!stripe || !elements) {
      return;
    }

    setIsProcessing(true);
    setErrorMessage('');

    try {
      // Create payment method using the separate elements
      const { paymentMethod, error } = await stripe.createPaymentMethod({
        type: 'card',
        card: elements.getElement(CardElement),
      });

      if (error) {
        console.error('Error creating payment method:', error);
        setErrorMessage(error.message);
        setIsProcessing(false);
        return;
      }

      console.log('card paymentMethod', paymentMethod);

      console.log('card element', elements.getElement(CardElement));

      console.log('Payment Method ID:', paymentMethod.id);

      console.log('details ', email, billingName, city);

      const idempotencyKey = crypto.randomUUID();
      console.log('Idempotency Key:', idempotencyKey);

      // Send to your backend
      const response = await fetch('http://localhost:8080/api/payment-gateway/initiate-payment', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          amount: total * 100, // amount in cents
          paymentMethodId: paymentMethod.id,
          idempotencyKey: idempotencyKey,
          customerEmail: email,
          currency: currency
        }),
      });

      const result = await response.json();
      console.log(result);
      
      if (response.ok) {
        console.log('Payment initiated successfully:', result);
        checkPaymentStatus(result.transactionId);
      } else {
        setErrorMessage(result.error || 'Payment failed. Please try again.');
      }
    } catch (err) {
      console.error('Payment error:', err);
      setErrorMessage('An unexpected error occurred. Please try again.');
    } finally {
      setIsProcessing(false);
    }
  };


  const checkPaymentStatus = async (transactionId) => {
    const response = await fetch(`http://localhost:8080/api/payment-gateway/getTransactionStatus?transactionId=${transactionId}`);

    const result = await response.json();
  
    if (result.status === "SUCCESS") {
      window.location.href = "https://www.sheffield.ac.uk/";
    } else if (result.status === "FAILED") {
      window.location.href = "/payment-failed";
    } else {
      setTimeout(() => checkPaymentStatus(transactionId), 1000); // retry after 2s
    }
  };
  


  return (
    
    <CardPaymentUI 
        handlePaymentRequest={handlePaymentRequest} 
        isProcessing={isProcessing} 
        errorMessage={errorMessage} 
        stripe={stripe}
        total={total}
        currency={currency} 
        setCurrency={setCurrency}
        currencySymbol={currencySymbol}
        email={email}
        setEmail={setEmail}
        billingName={billingName}
        setBillingName={setBillingName}
        city={city}
        setCity={setCity}

    />
  
  );
}