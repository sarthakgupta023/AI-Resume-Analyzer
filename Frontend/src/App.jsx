import { ProductCard, SidePanel } from 'kd-lib-krishna'
import { useState } from 'react'
import './App.css'

function App() {
  const [count, setCount] = useState(0)

  return (
    <>
      <ProductCard/>
      <SidePanel/>
      
    </>
  )
}

export default App
